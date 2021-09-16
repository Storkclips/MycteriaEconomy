package me.wmorales01.mycteriaeconomy.models;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.mojang.datafixers.util.Pair;
import me.wmorales01.mycteriaeconomy.inventories.NPCShopHolder;
import me.wmorales01.mycteriaeconomy.util.GUIUtil;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NPCVendor extends AbstractNPCShop implements VendingShop {
    private double profit;

    public NPCVendor(EntityPlayer entityPlayer, String npcName) {
        super(entityPlayer, npcName, ShopType.VENDING);
    }

    public NPCVendor(EntityPlayer entityPlayer, List<Pair<EnumItemSlot, ItemStack>> equipment, Hologram hologram,
                     List<Chest> linkedChests, List<ShopItem> shopItems, double profit) {
        super(linkedChests, shopItems, ShopType.VENDING, entityPlayer, equipment, hologram);
        this.profit = profit;
    }

    @Override
    public Inventory getShopGUI() {
        updateShopItemsStock();
        int inventorySize = GUIUtil.getFramedInventorySize(getShopItems().size());
        Inventory inventory = Bukkit.createInventory(new NPCShopHolder(this, true), inventorySize, getNpcName());
        GUIUtil.setFrame(inventory, Material.YELLOW_STAINED_GLASS_PANE);
        addShopItemsToInventory(inventory, true);
        GUIUtil.fillEmpty(inventory, Material.CYAN_STAINED_GLASS_PANE);
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
        Inventory inventory = Bukkit.createInventory(new NPCShopHolder(this, false), inventorySize, "Configuring " + getNpcName());
        GUIUtil.setFrame(inventory, Material.YELLOW_STAINED_GLASS_PANE);
        addShopItemsToInventory(inventory, false);
        GUIUtil.fillEmpty(inventory, Material.CYAN_STAINED_GLASS_PANE);
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
                getProfit(), Arrays.asList("&aClick to collect the vendor's profits.")));
    }

    /**
     * Adds information items about the Owner GUI to the passed inventory.
     *
     * @param inventory Inventory where the info items will be added to.
     */
    private void addOwnerGUIInfoItems(Inventory inventory) {
        List<String> lore = new ArrayList<>();
        lore.add("&a- &eClick on any of the items from your inventory to add them ");
        lore.add("&eto the Vendor.");
        lore.add("&a- &eClick on any of the shop items to modify their sell amount or price.");
        lore.add("&a- &eShift + Click on any of the shop items to access their internal stock.");
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
