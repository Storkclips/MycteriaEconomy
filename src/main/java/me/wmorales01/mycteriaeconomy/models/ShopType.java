package me.wmorales01.mycteriaeconomy.models;

public enum ShopType {
    VENDING,
    TRADING;

    public static ShopType fromName(String name) {
        ShopType shopType;
        try {
            shopType = ShopType.valueOf(name.toUpperCase());
        } catch (Exception e) {
            return null;
        }
        return shopType;
    }
}
