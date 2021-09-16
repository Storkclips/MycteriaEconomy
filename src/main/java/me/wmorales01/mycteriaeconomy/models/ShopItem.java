package me.wmorales01.mycteriaeconomy.models;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.util.StringUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopItem {
    // NamespacedKey that will be used to check if an ItemStack is a Machine Item
    private static final NamespacedKey MACHINE_ITEM_KEY = new NamespacedKey(MycteriaEconomy.getInstance(), "machine_item");
    // Data Container that will be used to store the MachineItem UUID
    private static final UUIDDataContainer UUID_DATA_CONTAINER = new UUIDDataContainer();

    private final UUID uuid;
    private final ItemStack itemStack;
    private double price;
    private int sellAmount;
    private int stock;

    public ShopItem(ItemStack itemStack, double price, int sellAmount) {
        this.uuid = UUID.randomUUID();
        this.itemStack = itemStack;
        this.price = price;
        this.sellAmount = sellAmount;
    }

    public ShopItem(UUID uuid, ItemStack itemStack, double price, int sellAmount) {
        this.uuid = uuid;
        this.itemStack = itemStack;
        this.price = price;
        this.sellAmount = sellAmount;
    }

    /**
     * Searches for the UUID key on the PersistentDataContainer of the passed item.
     *
     * @param item The item that will be searched for the UUID key on its PersistentDataContainer.
     * @return The contained Machine Item UUID of the item, null if it doesn't contain any.
     */
    public static UUID getUuidFromItemStack(ItemStack item) {
        if (!isMachineItem(item)) return null;

        return item.getItemMeta().getPersistentDataContainer().get(MACHINE_ITEM_KEY, UUID_DATA_CONTAINER);
    }

    /**
     * Checks if the passed item has the MACHINE_ITEM_KEY, if it does, return true.
     *
     * @param item ItemStack that will be checked.
     * @return True if the passed ItemStack is a MachineItem.
     */
    public static boolean isMachineItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        return item.getItemMeta().getPersistentDataContainer().has(MACHINE_ITEM_KEY, UUID_DATA_CONTAINER);
    }

    public UUID getUuid() {
        return uuid;
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    /**
     * Creates an ItemStack with the Machine Item Key and a lore detailing price, stock and sell amount to use on the
     * shop GUI.
     * <p>
     * Depending if it is a Vending Item or not the item will have lore indicating to click differently to buy or sell
     * different ammounts.
     *
     * @param isVendingItem true if the item will be exhibited in a Vending Machine, false if it will be exhibited on a
     *                      Trading Machine.
     * @return ItemStack with all the Machine Item's info.
     */
    public ItemStack getExhibitionItem(boolean isVendingItem) {
        ItemStack item = getInfoItem();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add("");
        String operation = isVendingItem ? "buy" : "sell";
        lore.add(StringUtil.formatColor("&2- &a&lLeft Click &ato " + operation + " 1."));
        lore.add(StringUtil.formatColor("&2- &a&lRight Click &ato " + operation + " 64."));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates and returns an ItemStack that contains the Machine Item key and a lore detailing price, stock and sell
     * amount.
     * <p>
     * Additionally, the item has a lore with instructions to configure the Machine Item in the Owner GUI.
     *
     * @return ItemStack with details of the MachineItem and instructions to configure it on the Owner GUI.
     */
    public ItemStack getConfigurationItem() {
        ItemStack item = getInfoItem();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add("");
        lore.add(StringUtil.formatColor("&2- &a&lLeft Click &ato modify this item's properties."));
        lore.add(StringUtil.formatColor("&2- &a&lRight Click &ato delete this item from the Machine."));
        meta.setLore(lore);
        ;
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates and returns an ItemStack with the price, sell amount and remaining stock of the Machine Item.
     *
     * @return ItemStack that indicates the basic information regarding the Machine Item.
     */
    private ItemStack getInfoItem() {
        ItemStack item = getItemStack();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        lore.add(StringUtil.formatColor("&ePrice: &3&l$" + StringUtil.roundNumber(price, 2)));
        lore.add(StringUtil.formatColor("&eSell Amount: &3&l" + sellAmount));
        lore.add(StringUtil.formatColor("&eStock: &3&l" + stock));
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(MACHINE_ITEM_KEY, new UUIDDataContainer(), uuid);
        item.setItemMeta(meta);
        return item;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(int sellAmount) {
        this.sellAmount = sellAmount;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
