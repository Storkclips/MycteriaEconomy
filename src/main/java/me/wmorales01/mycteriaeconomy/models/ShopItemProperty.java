package me.wmorales01.mycteriaeconomy.models;

import org.bukkit.Material;

public enum ShopItemProperty {
    SELL_AMOUNT(Material.PRISMARINE_CRYSTALS),
    PRICE(Material.EMERALD);

    private final Material icon;

    ShopItemProperty(Material icon) {
        this.icon = icon;
    }

    public static ShopItemProperty fromIcon(Material icon) {
        for (ShopItemProperty shopItemProperty : ShopItemProperty.values()) {
            if (icon != shopItemProperty.getIcon()) continue;

            return shopItemProperty;
        }
        return null;
    }

    public Material getIcon() {
        return icon;
    }
}
