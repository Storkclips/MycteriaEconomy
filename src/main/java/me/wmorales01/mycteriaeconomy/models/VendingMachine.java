package me.wmorales01.mycteriaeconomy.models;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.inventories.MachineHolder;
import me.wmorales01.mycteriaeconomy.util.BalanceManager;
import me.wmorales01.mycteriaeconomy.util.Framer;
import me.wmorales01.mycteriaeconomy.util.Messager;

public class VendingMachine extends Machine {
    private double profit;

    public VendingMachine(Location location) {
        super(location);
    }

    public VendingMachine(Player owner, Location location) {
        super(owner.getUniqueId(), location);
        profit = 0;
    }

    public VendingMachine(UUID ownerUUID, Location location, List<MachineItem> stock, List<Location> chestLocations,
                          double profit) {
        super(ownerUUID, location, stock, chestLocations);
        this.profit = profit;
    }

    public VendingMachine(Location location, List<Location> chestLocations, List<MachineItem> stock) {
        super(location, chestLocations, stock);
    }

    public static ItemStack getItemStack() {
        ItemStack vendingMachine = new ItemStack(Material.DROPPER);
        ItemMeta meta = vendingMachine.getItemMeta();

        meta.setDisplayName(ChatColor.RESET + "Vending Machine");
        vendingMachine.setItemMeta(meta);

        return vendingMachine;
    }

    public void addStock(MachineItem item) {
        getStock().add(item);
    }

    public void removeStock(MachineItem item) {
        getStock().remove(item);
    }

    public void addProfit(double profit) {
        this.profit += profit;
    }

    public void withdrawProfit() {
        Player player = Bukkit.getPlayer(getOwnerUUID());
        BalanceManager.giveBalance(player, profit);
        DecimalFormat format = new DecimalFormat("#.##");
        Messager.sendMessage(player, "&aYou received &e&l" + format.format(profit) + "$ &afrom this " +
                "Vending Machine.");

        this.profit = 0;
    }

    @Override
    public void registerMachine() {
        MycteriaEconomy plugin = MycteriaEconomy.getInstance();
        plugin.getVendingMachines().add(this);
    }

    @Override
    public Inventory getSellingGUI() {
        Inventory inventory = Bukkit.createInventory(new MachineHolder(getLocation()), 54, "");
        ((MachineHolder) inventory.getHolder()).setBalance(0);

        Framer.setInventoryFrame(inventory, Material.LIME_STAINED_GLASS_PANE);
        setStockInventory(inventory);

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.AQUA + "INFO");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Put your wallet on the side to use the machine.");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inventory.setItem(50, item);
        inventory.setItem(48, null);

        return inventory;
    }

    @Override
    public Inventory getOwnerGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(new MachineHolder(getLocation()), 54, "Editing Mode");

        Framer.setInventoryFrame(inventory, Material.LIME_STAINED_GLASS_PANE);
        setStockInventory(inventory);

        ItemStack item = new ItemStack(Material.SUNFLOWER);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.GREEN + "Collect profit");
        List<String> lore = new ArrayList<>();
        DecimalFormat format = new DecimalFormat("#.##");
        lore.add(ChatColor.YELLOW + "Profit: " + format.format(profit));
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
                    "&cThere are items that weren't added to the stock view because it is currently full.");
            break;
        }
        return inventory;
    }

    public static VendingMachine getVendingMachine(Location location) {
        MycteriaEconomy plugin = MycteriaEconomy.getPlugin(MycteriaEconomy.class);
        for (VendingMachine machine : plugin.getVendingMachines()) {
            if (!machine.getLocation().equals(location))
                continue;

            return machine;
        }

        return null;
    }

    public double getProfit() {
        return profit;
    }
}
