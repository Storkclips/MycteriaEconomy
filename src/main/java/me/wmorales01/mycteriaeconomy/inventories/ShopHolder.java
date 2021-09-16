package me.wmorales01.mycteriaeconomy.inventories;

import me.wmorales01.mycteriaeconomy.models.AbstractShop;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ShopHolder implements InventoryHolder {
    private final AbstractShop shop;
    private final boolean isShopGUI;

    public ShopHolder(AbstractShop shop, boolean isShopGUI) {
        this.shop = shop;
        this.isShopGUI = isShopGUI;
    }

    public AbstractShop getShop() {
        return shop;
    }

    public boolean isShopGUI() {
        return isShopGUI;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
