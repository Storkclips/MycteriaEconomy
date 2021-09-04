package me.wmorales01.mycteriaeconomy.listeners;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.inventories.ATMHolder;
import me.wmorales01.mycteriaeconomy.models.ATM;
import me.wmorales01.mycteriaeconomy.models.EconomyItem;
import me.wmorales01.mycteriaeconomy.models.EconomyPlayer;
import me.wmorales01.mycteriaeconomy.util.Messager;
import me.wmorales01.mycteriaeconomy.util.SFXManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ATMHandler implements Listener {
    private final MycteriaEconomy plugin;
    // Stores the players that were asked for confirmation to break an ATM
    private final Set<Player> atmBreakConfirmations;

    public ATMHandler(MycteriaEconomy plugin) {
        this.plugin = plugin;
        this.atmBreakConfirmations = new HashSet<>();
    }

    // Listens when a player places a block, if the placed ItemStack is an ATM then it creates a new ATM on that
    // location
    @EventHandler
    public void onATMPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack handItem = event.getItemInHand();
        if (!handItem.isSimilar(ATM.getItemStack())) return;
        if (!player.hasPermission("economyplugin.createatm")) {
            Messager.sendNoPermissionMessage(player);
            return;
        }
        Location location = event.getBlockPlaced().getLocation();
        new ATM(location).registerATM();
        SFXManager.playWorldSound(location, Sound.BLOCK_NOTE_BLOCK_BIT, 0.6F, 1.8F);
    }

    // Listens when a player right clicks a block, if it is a registered ATM then open the ATM GUI
    @EventHandler
    public void onATMOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block rightClickedBlock = event.getClickedBlock();
        if (rightClickedBlock.getType() != Material.DISPENSER) return;

        Location blockLocation = rightClickedBlock.getLocation();
        ATM atm = ATM.fromLocation(blockLocation);
        if (atm == null) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        EconomyPlayer economyPlayer = EconomyPlayer.fromPlayer(player);
        event.getPlayer().openInventory(atm.getWithdrawATMGUI(economyPlayer));
    }

    // Listens when a player clicks an inventory, if it is an ATM GUI then handle it according to the executed
    // operation
    @EventHandler
    public void onATMGUIClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!(event.getInventory().getHolder() instanceof ATMHolder)) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        EconomyPlayer economyPlayer = EconomyPlayer.fromPlayer(player);
        Inventory clickedInventory = event.getClickedInventory();
        ATM atm = ((ATMHolder) event.getInventory().getHolder()).getAtm();
        if (clickedItem == null) return;
        if (!EconomyItem.isEconomyItem(clickedItem)) return;
        if (clickedInventory.getType() == InventoryType.CHEST) { // Is withdrawing money
            withdrawBankBalance(atm, economyPlayer, clickedItem);
        } else {
            depositBankBalance(atm, economyPlayer, clickedItem);
        }
    }

    // Withdraws the value of the passed economyItem from the passed economyPlayer's bank balance (if possible)
    // and reopens the passed atm's GUI
    private void withdrawBankBalance(ATM atm, EconomyPlayer economyPlayer, ItemStack economyItem) {
        Player player = economyPlayer.getPlayer();
        double withdrawValue = EconomyItem.getValueFromItem(economyItem);
        if (economyPlayer.getBankBalance() - withdrawValue <= 0) {
            Messager.sendErrorMessage(player, "&cYou don't have enough bank balance to execute this transaction.");
            return;
        }
        economyPlayer.decreaseBankBalance(withdrawValue);
        ItemStack withdrawnItem = EconomyItem.getItemFromValue(withdrawValue);
        if (withdrawnItem == null) return;
        Location playerLocation = player.getLocation();
        if (player.getInventory().firstEmpty() == -1) {
            playerLocation.getWorld().dropItemNaturally(playerLocation, withdrawnItem);
        } else {
            player.getInventory().addItem(withdrawnItem);
        }
        SFXManager.playWorldSound(playerLocation, Sound.BLOCK_NOTE_BLOCK_BIT, 0.7F, 1.3F);
        player.openInventory(atm.getWithdrawATMGUI(economyPlayer));
    }

    // Deposit the passed balanceItem's value to the passed economyPlayer's bank account and reopens the
    // passed atm's GUI after deleting the clicked economyItem
    private void depositBankBalance(ATM atm, EconomyPlayer economyPlayer, ItemStack economyItem) {
        double depositValue = EconomyItem.getValueFromItem(economyItem);
        economyPlayer.increaseBankBalance(depositValue * economyItem.getAmount());
        economyItem.setAmount(0);

        Player player = economyPlayer.getPlayer();
        player.openInventory(atm.getWithdrawATMGUI(economyPlayer));
        SFXManager.playWorldSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.7F, 1.3F);
    }

    // Listens when a player breaks a block, if the block is an ATM and the player has the required permissions
    // then call the destroyATM method
    @EventHandler
    public void onATMBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location blockLocation = block.getLocation();
        ATM atm = ATM.fromLocation(blockLocation);
        if (atm == null) return;
        if (!player.hasPermission("economyplugin.createatm")) {
            Messager.sendNoPermissionMessage(player);
            return;
        }
        destroyATM(player, atm, event);
    }

    // Attemps to destroy the passed ATM, if the player hasn't been asked, ask for confirmation to delete the
    // ATM, if the player has been asked then destroy the ATM block and remove the ATM from the local database
    private void destroyATM(Player player, ATM atm, Cancellable event) {
        if (!atmBreakConfirmations.contains(player)) {
            event.setCancelled(true);
            atmBreakConfirmations.add(player);
            Bukkit.getScheduler().runTaskLater(plugin, () -> atmBreakConfirmations.remove(player), 100L);
            Messager.sendMessage(player, "&6Break the ATM again to confirm its deletion.");
            SFXManager.playPlayerSound(player, Sound.UI_BUTTON_CLICK, 0.8F, 1.3F);
            return;
        }
        atmBreakConfirmations.remove(player);
        atm.unregisterATM();
        SFXManager.playWorldSound(atm.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.7F, 1.8F);
        Messager.sendMessage(player, "&aATM successfully deleted.");
    }
}
