package me.wmorales01.mycteriaeconomy.models;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public abstract class Machine {
    private UUID ownerUUID;
    private Location location;
    private List<MachineItem> configuredStock;
    private List<Location> chestLocations;
    private List<Chest> chests;

    public Machine() {

    }

    public Machine(UUID ownerUUID, Location location) {
        this.ownerUUID = ownerUUID;
        this.location = location;
        this.configuredStock = new ArrayList<>();
        this.chestLocations = new ArrayList<>();
        this.chests = new ArrayList<>();
    }

    public Machine(UUID ownerUUID, Location location, List<MachineItem> configuredStock, List<Location> chestLocations) {
        this.ownerUUID = ownerUUID;
        this.location = location;
        this.configuredStock = configuredStock;
        this.chestLocations = chestLocations;
        this.chests = new ArrayList<>();
    }

    public Machine(Location location) {
        this.location = location;
        this.configuredStock = new ArrayList<>();
        this.chestLocations = new ArrayList<>();
        this.chests = new ArrayList<>();
    }

    public Machine(Location location, List<Location> chestLocations, List<MachineItem> configuredStock) {
        this.location = location;
        this.configuredStock = configuredStock;
        this.chestLocations = chestLocations;
        this.chests = new ArrayList<>();
    }

    public abstract Inventory getSellingGUI();

    public abstract Inventory getOwnerGUI(Player player);

    public abstract void registerMachine();

    public void openSellGUI(Player player) {
        List<Chest> chests = new ArrayList<>();
        for (Location location : chestLocations)
            chests.add((Chest) location.getBlock().getState());

        this.chests = chests;
        player.openInventory(getSellingGUI());
    }

    public void openOwnerGUI(Player player) {
        List<Chest> chests = new ArrayList<>();
        for (Location location : chestLocations) {
            chests.add((Chest) location.getBlock().getState());
        }

        this.chests = chests;
        player.openInventory(getOwnerGUI(player));
    }

    public void setStockInventory(Inventory inventory) {
        if (configuredStock.isEmpty())
            return;

        for (MachineItem machineItem : configuredStock) {
            if (machineItem == null)
                continue;

            machineItem.setStockAmount(getChestStock(machineItem));
            ItemStack item = machineItem.getSellItem(false);
            inventory.addItem(item);
        }
    }

    public MachineItem getMachineItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            lore.clear();
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        for (MachineItem machineItem : configuredStock) {
            if (machineItem == null)
                continue;
            if (!machineItem.getItemStack().isSimilar(item))
                continue;

            return machineItem;
        }
        return null;
    }

    public Map<ItemStack, Integer> getChestStock() {
        Map<ItemStack, Integer> chestStock = new HashMap<>();
        int itemAmount;
        for (Inventory inventory : getChestInventories())
            for (ItemStack item : inventory.getContents()) {
                if (item == null || item.getType().isAir())
                    continue;

                item = item.clone();
                itemAmount = item.getAmount();
                item.setAmount(1);
                if (chestStock.containsKey(item))
                    chestStock.put(item, chestStock.get(item) + itemAmount);
                else
                    chestStock.put(item, itemAmount);
            }
        return chestStock;
    }

    public int getChestStock(MachineItem machineItem) {
        ItemStack item = machineItem.getItemStack();
        int amount = 0;
        for (Inventory inventory : getChestInventories())
            for (ItemStack chestItem : inventory.getContents()) {
                if (chestItem == null || chestItem.getType().isAir())
                    continue;

                ItemStack checkItem = chestItem.clone();
                if (!item.isSimilar(checkItem))
                    continue;

                amount += chestItem.getAmount();
            }
        return amount;
    }

    private List<Inventory> getChestInventories() {
        List<Inventory> inventories = new ArrayList<>();
        for (Chest chest : chests) {
            Inventory inventory = chest.getInventory();
            if (inventory.getHolder() instanceof DoubleChestInventory)
                inventory = ((DoubleChest) inventory.getHolder()).getInventory();

            inventories.add(inventory);
        }
        return inventories;
    }

    public boolean isWorking() {
        if (chestLocations == null || chestLocations.isEmpty())
            return false;
        for (Location location : chestLocations)
            if (location.getBlock().getState() instanceof Chest)
                return true;

        return false;
    }

    public boolean isMachineItem(ItemStack item) {
        return getMachineItem(item) != null;
    }

    public boolean isStockFull() {
        for (Inventory inventory : getChestInventories())
            if (inventory.firstEmpty() == -1)
                return true;

        return false;
    }

    public void addStock(MachineItem machineItem) {
        configuredStock.add(machineItem);
    }

    public void increaseStock(MachineItem machineItem, int amount) {
        ItemStack item = machineItem.getItemStack().clone();
        item.setAmount(amount);
        machineItem.addStockAmount(amount);
        for (Inventory inventory : getChestInventories()) {
            if (inventory.firstEmpty() == -1)
                continue;

            inventory.addItem(item);
            break;
        }
    }

    public void discountStock(MachineItem machineItem, int amount) {
        ItemStack item = machineItem.getItemStack().clone();
        item.setAmount(amount);
        machineItem.discountStockAmount(amount);
        for (Inventory inventory : getChestInventories()) {
            if (!inventory.containsAtLeast(item, amount))
                continue;

            inventory.removeItem(item);
            break;
        }
    }

    public void removeStock(MachineItem machineItem) {
        configuredStock.remove(machineItem);
    }

    public static Machine getMachineAtLocation(Location location) {
        MycteriaEconomy plugin = MycteriaEconomy.getPlugin(MycteriaEconomy.class);
        for (VendingMachine vendingMachine : plugin.getVendingMachines()) {
            if (!location.equals(vendingMachine.getLocation()))
                continue;

            return vendingMachine;
        }
        for (TradingMachine tradingMachine : plugin.getTradingMachines()) {
            if (!location.equals(tradingMachine.getLocation()))
                continue;

            return tradingMachine;
        }
        for (NPCShop npcShop : plugin.getNpcs()) {
            if (!location.equals(npcShop.getLocation()))
                continue;

            return npcShop;
        }
        return null;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<MachineItem> getStock() {
        return configuredStock;
    }

    public void setStock(List<MachineItem> stock) {
        this.configuredStock = stock;
    }

    public List<Chest> getChests() {
        return chests;
    }

    public List<Location> getChestLocations() {
        return chestLocations;
    }
}
