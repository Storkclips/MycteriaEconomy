package me.wmorales01.mycteriaeconomy.models;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.inventories.MachineHolder;
import me.wmorales01.mycteriaeconomy.util.BalanceManager;
import me.wmorales01.mycteriaeconomy.util.Framer;
import me.wmorales01.mycteriaeconomy.util.Messager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class TradingMachine extends Machine {
    private double machineBalance;

    public TradingMachine() {

    }

    public TradingMachine(Player owner, Location location) {
        super(owner.getUniqueId(), location);
        setMachineBalance(0);
    }

    public TradingMachine(UUID ownerUuid, Location location, List<MachineItem> stock, List<Location> chestLocations,
                          double balance) {
        super(ownerUuid, location, stock, chestLocations);
        this.setMachineBalance(balance);
    }

    public static ItemStack getItemStack() {
        ItemStack machine = new ItemStack(Material.DISPENSER);
        ItemMeta meta = machine.getItemMeta();

        meta.setDisplayName(ChatColor.RESET + "Trading Machine");
        machine.setItemMeta(meta);

        return machine;
    }

    public double getMachineBalance() {
        return machineBalance;
    }

    public void addMachineBalance(double amount) {
        machineBalance += amount;
    }

    public void reduceMachineBalance(double amount) {
        machineBalance -= amount;
    }

    public void setMachineBalance(double balance) {
        this.machineBalance = balance;
    }

    public void withdrawMachineBalance() {
        Player player = Bukkit.getPlayer(getOwnerUUID());

        BalanceManager.giveBalance(player, machineBalance);
        DecimalFormat format = new DecimalFormat("#.##");
        Messager.sendMessage(player, "&aYou received &e&l" + format.format(machineBalance) + "$ &afrom this" +
                " Trading Machine.");

        machineBalance = 0;
    }

    @Override
    public void registerMachine() {
        MycteriaEconomy plugin = MycteriaEconomy.getInstance();
        plugin.getTradingMachines().add(this);
    }

    @Override
    public Inventory getOwnerGUI(Player player) {
        OfflinePlayer owner = Bukkit.getOfflinePlayer(getOwnerUUID());
        Inventory inventory = Bukkit.createInventory(new MachineHolder(getLocation()), 54);

        Framer.setInventoryFrame(inventory, Material.LIME_STAINED_GLASS_PANE);
        setStockInventory(inventory);

        ItemStack item = new ItemStack(Material.SUNFLOWER);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.GREEN + "Collect balance");
        List<String> lore = new ArrayList<>();
        DecimalFormat format = new DecimalFormat("#.##");
        lore.add(ChatColor.YELLOW + "Balance: " + format.format(machineBalance));
        meta.setLore(lore);
        item.setItemMeta(meta);

        inventory.setItem(49, item);

        for (Entry<ItemStack, Integer> entry : getChestStock().entrySet()) {
            item = entry.getKey().clone();
            meta = item.getItemMeta();
            int amount = entry.getValue();
            if (isMachineItem(item))
                continue;

            lore.clear();
            lore.add(ChatColor.BLUE + "Amount: " + amount);
            meta.setLore(lore);
            item.setItemMeta(meta);
            Map<Integer, ItemStack> remaining = inventory.addItem(item);
            if (remaining.isEmpty())
                continue;

            Messager.sendErrorMessage(player,
                    "&cThere are items that weren't added to the edit view because it is currently full.");
            break;
        }
        return inventory;
    }

    @Override
    public Inventory getSellingGUI() {
        OfflinePlayer owner = Bukkit.getOfflinePlayer(getOwnerUUID());
        Inventory inventory = Bukkit.createInventory(new MachineHolder(getLocation()), 54);
        ((MachineHolder) inventory.getHolder()).setBalance(0);

        Framer.setInventoryFrame(inventory, Material.LIME_STAINED_GLASS_PANE);
        setStockInventory(inventory);

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.AQUA + "INFO");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GOLD
                + "Put your wallet on the side to use the machine.");
        lore.add(ChatColor.GOLD + "Click any of the offers to sell the selected item.");
        meta.setLore(lore);
        item.setItemMeta(meta);

        inventory.setItem(49, item);

        item.setType(Material.SUNFLOWER);
        DecimalFormat format = new DecimalFormat("#.##");
        meta.setDisplayName(ChatColor.YELLOW + "Machine Balance: " + format.format(machineBalance));
        lore.clear();
        meta.setLore(lore);
        item.setItemMeta(meta);
        inventory.setItem(48, item);
        inventory.setItem(50, null);

        return inventory;
    }
}
