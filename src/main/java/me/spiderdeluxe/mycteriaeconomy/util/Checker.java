package me.spiderdeluxe.mycteriaeconomy.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Checker {

    public static boolean isBill(final ItemStack item) {
        if (item == null)
            return false;
        if (isSimilar(item, CurrencyItem.oneDollarBill()))
            return true;
        if (isSimilar(item, CurrencyItem.fiveDollarBill()))
            return true;
        if (isSimilar(item, CurrencyItem.tenDollarBill()))
            return true;
        if (isSimilar(item, CurrencyItem.twentyDollarBill()))
            return true;
        if (isSimilar(item, CurrencyItem.fiftyDollarBill()))
            return true;
        return isSimilar(item, CurrencyItem.oneHundredDollarBill());
    }

    public static boolean isCoin(final ItemStack item) {
        if (item == null)
            return false;
        if (isSimilar(item, CurrencyItem.oneCentCoin()))
            return true;
        if (isSimilar(item, CurrencyItem.fiveCentCoin()))
            return true;
        if (isSimilar(item, CurrencyItem.tenCentCoin()))
            return true;
        return isSimilar(item, CurrencyItem.twentyFiveCentCoin());
    }

    // Returns if the passed checkItem is similar to the target item without taking into account the item quantity
    public static boolean isSimilar(final ItemStack firstItem, final ItemStack secondItem) {
        if (secondItem == null || firstItem == null) return false;

        if (firstItem.getType() != secondItem.getType()) return false;

        if (!firstItem.hasItemMeta() ||
                !secondItem.hasItemMeta()) return false;

        final ItemMeta firstMeta = firstItem.getItemMeta();
        final ItemMeta secondMeta = secondItem.getItemMeta();

        if (!firstMeta.hasCustomModelData()
                || !secondMeta.hasCustomModelData()) return false;

        return firstMeta.getCustomModelData() == secondMeta.getCustomModelData();
    }


    public static ItemStack findSimiliarInInv(final Inventory inventory, final ItemStack firstItem) {
        for (final ItemStack secondItem : inventory.getContents()) {
            if (firstItem == null) return null;

            if (secondItem != null)
                if (isSimilar(firstItem.clone(), secondItem.clone())) {
                    return secondItem;
                }
        }
        return null;
    }
}
