package me.wmorales01.mycteriaeconomy.models;

import org.bukkit.inventory.ItemStack;

/**
 * This class is used when operations of adding new Machine Items to Machines or NPCShops are needed.
 */

public class ShopItemAdder {
    private final AbstractShop shop;
    private final ItemStack newItem;

    public ShopItemAdder(AbstractShop shop, ItemStack newItem) {
        this.shop = shop;
        this.newItem = newItem;
    }

    public AbstractShop getShop() {
        return shop;
    }

    public ItemStack getNewItem() {
        return newItem;
    }
}
