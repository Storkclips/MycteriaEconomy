package me.wmorales01.mycteriaeconomy.models;

import me.wmorales01.mycteriaeconomy.inventories.MachineHolder;
import me.wmorales01.mycteriaeconomy.util.GUIUtil;
import me.wmorales01.mycteriaeconomy.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class VendingMachine extends AbstractMachine implements VendingShop {
    private double profit; // Amount of money collected by the machine through sells

    public VendingMachine(Player owner, Block machineBlock) {
        super(owner, machineBlock);
        this.profit = 0;
    }

    public VendingMachine(List<Chest> linkedChests, List<ShopItem> shopItems, UUID machineUuid, UUID ownerUuid,
                          Location machineLocation, double profit) {
        super(linkedChests, shopItems, machineUuid, ownerUuid, machineLocation);
        this.profit = profit;
    }

    /**
     * Returns the ItemStack corresponding to a Vending Machine
     *
     * @return the ItemStack of a Vending Machine
     */
    public static ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.DISPENSER);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(StringUtil.formatColor("&rVending Machine"));
        lore.add(StringUtil.formatColor("&ePlace this block to install a Vending Machine."));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Inventory getShopGUI() {
        updateShopItemsStock();
        int inventorySize = GUIUtil.getFramedInventorySize(getShopItems().size());
        Inventory inventory = Bukkit.createInventory(new MachineHolder(this, true), inventorySize, "Vending Machine");
        GUIUtil.setFrame(inventory, Material.LIME_STAINED_GLASS_PANE);
        addShopItemsToInventory(inventory, true);
        GUIUtil.fillEmpty(inventory, Material.YELLOW_STAINED_GLASS_PANE);
        inventory.setItem(inventorySize - 5, null); // Opening Wallet Slot
        addShopGUIInfoItems(inventory);
        return inventory;
    }

    /**
     * Adds information items to the bottom of the passed inventory that indicates what can be done in the Shop GUI.
     *
     * @param inventory Inventory where info items will be added to.
     */
    private void addShopGUIInfoItems(Inventory inventory) {
        List<String> infoLore = new ArrayList<>();
        infoLore.add("&aClick any item to buy it and add it to your inventory.");
        infoLore.add("&aThe cost of the item will be paid with your inventory's cash.");
        infoLore.add("&aIf you put a wallet on the empty slot the cash will be ");
        infoLore.add("&ataken directly from it instead of your inventory.");
        inventory.setItem(inventory.getSize() - 6, GUIUtil.getGUIItem(Material.COMPASS, "&2Instructions", infoLore));
    }

    @Override
    public Inventory getOwnerGUI() {
        updateShopItemsStock();
        int inventorySize = GUIUtil.getFramedInventorySize(getShopItems().size());
        Inventory inventory = Bukkit.createInventory(new MachineHolder(this, false), inventorySize, "Configuring Vending Machine");
        GUIUtil.setFrame(inventory, Material.LIME_STAINED_GLASS_PANE);
        addShopItemsToInventory(inventory, false);
        GUIUtil.fillEmpty(inventory, Material.YELLOW_STAINED_GLASS_PANE);
        addOwnerGUIProfitItem(inventory);
        addOwnerGUIInfoItems(inventory);
        return inventory;
    }

    /**
     * Adds an ItemStack that displays the machine's profit and instructions to collect them.
     *
     * @param inventory Inventory where the Profits item will be added to.
     */
    private void addOwnerGUIProfitItem(Inventory inventory) {
        inventory.setItem(inventory.getSize() - 5, GUIUtil.getGUIItem(Material.SUNFLOWER, "&eProfit: &3&l$" +
                getProfit(), Arrays.asList("&aClick to collect your profits.")));
    }

    /**
     * Adds information items about the Owner GUI to the passed inventory.
     *
     * @param inventory Inventory where the info items will be added to.
     */
    private void addOwnerGUIInfoItems(Inventory inventory) {
        List<String> lore = new ArrayList<>();
        lore.add("&a- &eClick on any of the items from your inventory to add them ");
        lore.add("&eto the Machine.");
        lore.add("&a- &eClick on any of the shop items to modify their sell amount or price.");
        inventory.setItem(inventory.getSize() - 6, GUIUtil.getGUIItem(Material.COMPASS, "&2Instructions", lore));
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public void increaseProfit(double amount) {
        profit += amount;
    }

    public void decreaseProfit(double amount) {
        profit -= amount;
    }
}
