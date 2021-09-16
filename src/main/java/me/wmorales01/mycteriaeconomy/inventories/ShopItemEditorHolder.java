package me.wmorales01.mycteriaeconomy.inventories;

import me.wmorales01.mycteriaeconomy.models.AbstractShop;
import me.wmorales01.mycteriaeconomy.models.ShopItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ShopItemEditorHolder implements InventoryHolder {
    private final ShopItem shopItem;
    private final AbstractShop shop;

    public ShopItemEditorHolder(ShopItem shopItem, AbstractShop shop) {
        this.shopItem = shopItem;
        this.shop = shop;
    }

    public ShopItem getShopItem() {
        return shopItem;
    }

    public AbstractShop getShop() {
        return shop;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
