package me.wmorales01.mycteriaeconomy.models;

public class ShopItemEditor {
    private final ShopItem shopItem;
    private final AbstractShop shop;
    private final ShopItemProperty shopItemProperty;

    public ShopItemEditor(ShopItem shopItem, AbstractShop shop, ShopItemProperty shopItemProperty) {
        this.shopItem = shopItem;
        this.shop = shop;
        this.shopItemProperty = shopItemProperty;
    }

    public ShopItem getMachineItem() {
        return shopItem;
    }

    public AbstractShop getShop() {
        return shop;
    }

    public ShopItemProperty getMachineItemProperty() {
        return shopItemProperty;
    }
}
