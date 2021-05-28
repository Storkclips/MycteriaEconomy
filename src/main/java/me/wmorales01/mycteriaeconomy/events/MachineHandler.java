package me.wmorales01.mycteriaeconomy.events;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.inventories.MachineHolder;
import me.wmorales01.mycteriaeconomy.inventories.NPCShopHolder;
import me.wmorales01.mycteriaeconomy.models.*;
import me.wmorales01.mycteriaeconomy.util.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class MachineHandler implements Listener {
    private MycteriaEconomy plugin;

    public MachineHandler(MycteriaEconomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVendingMachinePlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled())
            return;
        if (!player.hasPermission("economyplugin.placemachine")) {
            Messager.sendNoPermissionMessage(player);
            return;
        }
        ItemStack placedItem = player.getInventory().getItem(event.getHand());
        Location location = event.getBlockPlaced().getLocation();
        ItemStack vendingMachine = VendingMachine.getItemStack();
        ItemStack tradingMachine = TradingMachine.getItemStack();
        Machine machine;
        if (placedItem.isSimilar(vendingMachine)) {
            machine = new VendingMachine(player, location);
            machine.registerMachine();
            location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 5);

        } else if (placedItem.isSimilar(tradingMachine)) {
            machine = new TradingMachine(player, location);
            machine.registerMachine();
            location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 5);
        }
    }

    @EventHandler
    public void onMachineLink(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        if (!plugin.getMachineLinkers().containsKey(player) || clickedBlock.getType() != Material.CHEST)
            return;

        plugin.getMachineLinkers().get(player).getChestLocations().add(clickedBlock.getLocation());
        plugin.getMachineLinkers().remove(player);
        Messager.sendSuccessMessage(player, "&aMachine linked!");
        event.setCancelled(true);
        player.getWorld().playSound(clickedBlock.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 2);
    }

    @EventHandler
    public void onMachineOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Block clickedBlock = event.getClickedBlock();

        Player player = event.getPlayer();
        Location blockLocation = clickedBlock.getLocation();
        Machine machine = Machine.getMachineAtLocation(blockLocation);
        if (machine == null)
            return;

        event.setCancelled(true);
        if (!machine.isWorking()) {
            Messager.sendMessage(player, "&cThis machine isn't working right now.");
            SFXManager.playPlayerSound(player, Sound.BLOCK_PISTON_CONTRACT, 1, 3);
            return;
        }
        if (player.isSneaking() && machine.getOwnerUUID().equals(player.getUniqueId()))
            machine.openOwnerGUI(player);
        else
            machine.openSellGUI(player);
    }

    @EventHandler
    public void onMachineGUIClick(InventoryClickEvent event) {
        if (event.getInventory() == null || event.getClickedInventory() == null)
            return;
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof MachineHolder))
            return;

        Player player = (Player) event.getWhoClicked();
        MachineHolder holder = ((MachineHolder) inventory.getHolder());
        Location machineLocation = holder.getLocation();
        Machine machine = Machine.getMachineAtLocation(machineLocation);
        Inventory clickedInventory = event.getClickedInventory();

        event.setCancelled(true);
        player.updateInventory();
        if (event.getView().getTitle().contains("Editing")) { // Owner actions
            if (clickedInventory.getType() == InventoryType.PLAYER && machine instanceof TradingMachine) {
                ItemStack clickedItem = event.getCurrentItem();
                if (Checker.isBill(clickedItem) || Checker.isCoin(clickedItem))
                    depositMachineBalance(player, (TradingMachine) machine, event);

            } else if (event.getClick().isKeyboardClick()) // Canceling click
                return;
            else if (event.getSlot() == 49) // Collecting money
                collectMoneyFromMachine(player, machine);
            else if (event.getCurrentItem() != null) // Modifying item
                modifyMachineItems(player, machine, event);

        } else { // Buyer actions
            int walletSlot = machine instanceof VendingMachine ? 48 : 50;
            Wallet wallet = Wallet.getByItemStack(inventory.getItem(walletSlot));
            if (clickedInventory.getType() == InventoryType.PLAYER && wallet == null) {
                // Adding wallet to machine slot
                ItemStack clickedItem = event.getCurrentItem();
                wallet = Wallet.getByItemStack(clickedItem);
                if (wallet == null) {
                    event.setCancelled(true);
                    return;
                }
                inventory.setItem(walletSlot, clickedItem.clone());
                player.getInventory().setItem(event.getSlot(), null);
                return;

            }
            if (clickedInventory.getType() != InventoryType.PLAYER && wallet != null && event.getSlot() == walletSlot) {
                // Giving wallet back to player
                if (player.getInventory().firstEmpty() == -1)
                    return;

                player.getInventory().addItem(inventory.getItem(walletSlot));
                inventory.setItem(walletSlot, null);
                return;
            }
            // Interacting with items
            if (machine instanceof VendingMachine)
                buyItem(player, holder, (VendingMachine) machine, event);
            else if (machine instanceof TradingMachine && clickedInventory.getType() != InventoryType.PLAYER)
                sellItem(player, holder, (TradingMachine) machine, event);
            else
                return;
        }
    }

    @EventHandler
    public void onMachineClose(InventoryCloseEvent event) {
        if (event.getInventory() == null)
            return;
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (!(inventory.getHolder() instanceof MachineHolder) && !(inventory.getHolder() instanceof NPCShopHolder))
            return;
        if (event.getView().getTitle().contains("Editing"))
            return;

        Location dropLocation = null;
        ItemStack drop = null;
        if (holder instanceof MachineHolder) {
            dropLocation = ((MachineHolder) holder).getLocation();
            Machine machine = Machine.getMachineAtLocation(dropLocation);
            if (machine instanceof VendingMachine && inventory.getItem(48) != null)
                drop = inventory.getItem(48);
            else if (machine instanceof TradingMachine && inventory.getItem(50) != null)
                drop = inventory.getItem(50);

        } else if (holder instanceof NPCShopHolder) {
            dropLocation = ((NPCShopHolder) holder).getNpcShop().getNpc().getBukkitEntity().getLocation();
            if (inventory.getItem(48) != null)
                drop = inventory.getItem(48);
        }
        if (dropLocation == null || drop == null || drop.getType().isAir())
            return;

        dropLocation.getWorld().dropItemNaturally(dropLocation, drop);
    }

    @EventHandler
    public void onMachineBreak(BlockBreakEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        Machine machine = Machine.getMachineAtLocation(blockLocation);
        if (machine == null)
            return;

        ItemStack dropItem = null;
        double balance = 0;
        if (machine instanceof VendingMachine) {
            plugin.getVendingMachines().remove(machine);
            dropItem = VendingMachine.getItemStack();
            balance = ((VendingMachine) machine).getProfit();

        } else if (machine instanceof TradingMachine) {
            plugin.getTradingMachines().remove(machine);
            dropItem = TradingMachine.getItemStack();
            balance = ((TradingMachine) machine).getMachineBalance();

        } else
            return;

        event.setDropItems(false);
        blockLocation.getWorld().dropItemNaturally(blockLocation, dropItem);
        blockLocation.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, blockLocation, 20);
        blockLocation.getWorld().playSound(blockLocation, Sound.ENTITY_GENERIC_EXPLODE, 1, 2);
        for (MachineItem machineItem : machine.getStock()) {
            if (machineItem == null)
                continue;

            ItemStack drop = machineItem.getItemStack();
            if (drop.getType() == Material.AIR)
                continue;
            drop.setAmount(machineItem.getStockAmount());
            if (drop.getAmount() == 0)
                continue;
            blockLocation.getWorld().dropItemNaturally(blockLocation, drop);
        }
        for (ItemStack item : BalanceManager.getBalanceItems(balance))
            blockLocation.getWorld().dropItemNaturally(blockLocation, item);
    }

    private void addStockToMachine(Player player, Machine machine, InventoryClickEvent event) {
        if (event.getCurrentItem() == null)
            return;
        ItemStack clickedItem = event.getCurrentItem().clone();
        if (Checker.isBill(clickedItem) || Checker.isCoin(clickedItem))
            return;

        clickedItem.setAmount(1);
        MachineOperator operator = new MachineOperator(player, machine, clickedItem);
        plugin.getVendingOperators().add(operator);
        Messager.sendMessage(player, "&aType the sell amount and price for this item in chat separated by \"-\".\n"
                + "&lEx. &a64 - 200\n" + "&6You can type \"cancel\" in any moment to cancel the process.");
        player.closeInventory();
    }

    private void collectMoneyFromMachine(Player player, Machine machine) {
        EconomyPlayer ecoPlayer = EconomyPlayer.fromPlayer(player);
        if (ecoPlayer == null) {
            ecoPlayer = new EconomyPlayer(player);
            plugin.addEconomyPlayers(ecoPlayer);
        }

        if (machine instanceof VendingMachine) {
            VendingMachine vendingMachine = (VendingMachine) machine;
            vendingMachine.withdrawProfit();

        } else if (machine instanceof TradingMachine) {
            TradingMachine tradingMachine = (TradingMachine) machine;
            tradingMachine.withdrawMachineBalance();
        }
        player.openInventory(machine.getOwnerGUI(player));
        Location machineLocation = machine.getLocation();
        machineLocation.getWorld().playSound(machineLocation, Sound.BLOCK_NOTE_BLOCK_BIT, 1, -1);
    }

    private <T extends Machine> void modifyMachineItems(Player player, T machine, InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (Checker.isFrame(clickedItem))
            return;

        ClickType click = event.getClick();
        MachineItem machineItem = machine.getMachineItem(clickedItem.clone());
        if (machineItem == null && click.isLeftClick()) { // Configuring item
            addStockToMachine(player, machine, event);
            return;
        }
//		if (click.isLeftClick()) {
//			 Addding stock

//			player.openInventory(machine.getItemStockGUI(machineItem));
        if (click.isRightClick()) {
            // Removing stock
            event.getClickedInventory().setItem(event.getSlot(), null);
            machine.removeStock(machineItem);
            player.updateInventory();
        }
    }

    private void depositMachineBalance(Player player, TradingMachine machine, InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        double value = 0;
        if (Checker.isBill(clickedItem))
            value = Getter.getValueFromBill(clickedItem);
        else if (Checker.isCoin(clickedItem))
            value = Getter.getValueFromCoin(clickedItem);
        else
            return;

        machine.addMachineBalance(value);
        player.getInventory().setItem(event.getSlot(), null);
        Location machineLocation = machine.getLocation();
        machineLocation.getWorld().playSound(machineLocation, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
        player.openInventory(machine.getOwnerGUI(player));
    }

    private void sellItem(Player player, MachineHolder holder, TradingMachine machine, InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        double machineBalance = machine.getMachineBalance();
        if (clickedItem == null)
            return;
        if (Checker.isFrame(clickedItem))
            return;
        MachineItem machineItem = machine.getMachineItem(clickedItem.clone());
        if (machineItem == null)
            return;

        Location machineLocation = machine.getLocation();
        ItemStack receivedItem = machineItem.getItemStack().clone();
        if (!player.getInventory().containsAtLeast(receivedItem, machineItem.getSellAmount())) {
            Messager.sendMessage(player, "&cYou don't have enough items to sell.");
            machineLocation.getWorld().playSound(machineLocation, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 2);
            return;
        }
        double buyPrice = machineItem.getPrice();
        if (machineBalance < buyPrice) {
            Messager.sendMessage(player, "&cThis machine does not have enough balance.");
            machineLocation.getWorld().playSound(machineLocation, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 2);
            return;
        }
        int sellAmount = machineItem.getSellAmount();
        if (machine.isStockFull()) {
            Messager.sendMessage(player, "&cThe stock for this item is full.");
            machineLocation.getWorld().playSound(machineLocation, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 2);
            return;
        }
        Inventory inventory = event.getInventory();
        Wallet wallet = Wallet.getByItemStack(inventory.getItem(50));
        if (wallet == null) {
            Messager.sendErrorMessage(player, "&cYou must put a wallet on the machine for it to work.");
            return;
        }
        // Updating machine
        Inventory clickedInventory = event.getClickedInventory();
        machine.increaseStock(machineItem, sellAmount);
        machine.reduceMachineBalance(buyPrice);
        clickedInventory.setItem(event.getSlot(), machineItem.getSellItem(false));

        // Giving buy money
        wallet.increaseBalance(buyPrice);
        inventory.setItem(50, null);
        Inventory newGUI = machine.getSellingGUI();
        newGUI.setItem(50, wallet.getItemStack());


        // Removing sold item
        receivedItem.setAmount(sellAmount);
        player.getInventory().removeItem(receivedItem);
        player.updateInventory();
        machineLocation.getWorld().playSound(machineLocation, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2);
        player.openInventory(newGUI);
    }

    private void buyItem(Player player, MachineHolder holder, VendingMachine machine, InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Wallet wallet = Wallet.getByItemStack(inventory.getItem(48));
        Inventory clickedInventory = event.getClickedInventory();
        Location machineLocation = machine.getLocation();
        ItemStack clickedItem = event.getCurrentItem();
        if (wallet == null) {
            Messager.sendErrorMessage(player, "&cYou must put a wallet on the machine for it to work.");
            return;
        }
        double balance = wallet.getBalance();
        if (clickedItem == null)
            return;
        if (Checker.isFrame(clickedItem))
            return;
        MachineItem machineItem = machine.getMachineItem(clickedItem.clone());
        if (machineItem == null)
            return;

        // Gathering item sell information
        int itemAmount = machineItem.getStockAmount();
        int sellAmount = machineItem.getSellAmount();
        if (itemAmount <= 0 || itemAmount - sellAmount < 0) {
            Messager.sendMessage(player, "&cThis item is out of stock.");
            machineLocation.getWorld().playSound(machineLocation, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 2);
            return;
        }
        double itemPrice = machineItem.getPrice();
        if (balance < itemPrice) {
            Messager.sendMessage(player, "&cYou don't have enough funds to execute this transaction.");
            machineLocation.getWorld().playSound(machineLocation, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 2);
            return;
        }
        // Purchasing the item
        machine.addProfit(itemPrice);
        machine.discountStock(machineItem, sellAmount);
        clickedInventory.setItem(event.getSlot(), machineItem.getSellItem(false));

        // Executing transaction
        ItemStack purchasedItem = machineItem.getItemStack().clone();
        wallet.discountBalance(itemPrice);
        inventory.setItem(48, wallet.getItemStack());
        purchasedItem.setAmount(sellAmount);
        player.getInventory().addItem(purchasedItem);
        machineLocation.getWorld().playSound(machineLocation, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2);
    }

    private void updateBalance(Wallet wallet, double amount) {

    }
}
