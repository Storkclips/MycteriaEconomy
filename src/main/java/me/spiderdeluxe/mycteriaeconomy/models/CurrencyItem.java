package me.spiderdeluxe.mycteriaeconomy.models;

import me.spiderdeluxe.mycteriaeconomy.MycteriaEconomyPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.ArrayList;
import java.util.List;

public class CurrencyItem {
    // Namespacedkey that will be used to store the economic value of each item on its persistent data container
    protected static final NamespacedKey VALUE_KEY = new NamespacedKey(MycteriaEconomyPlugin.getInstance(), "value");

    public static List<ItemStack> getEconomyItems() {
        return new ArrayList<>() {
            private static final long serialVersionUID = 2352388802142912530L;

            {
                add(oneDollarBill());
                add(fiveDollarBill());
                add(tenDollarBill());
                add(twentyDollarBill());
                add(fiftyDollarBill());
                add(oneHundredDollarBill());
                add(oneCentCoin());
                add(fiveCentCoin());
                add(tenCentCoin());
                add(twentyFiveCentCoin());
            }
        };
    }


    public static ItemStack baseBill(final String type, final double value, final int cmd) {
        final ItemStack bill = new ItemStack(Material.PAPER);

        final ItemMeta meta = bill.getItemMeta();
        meta.setCustomModelData(cmd);
        meta.getPersistentDataContainer().set(VALUE_KEY, PersistentDataType.DOUBLE, value);
        bill.setItemMeta(meta);
        return  ItemCreator.of(CompMaterial.PAPER)
                .name(ItemUtil.bountifyCapitalized(type) + " Dollar Bill")
                .meta(meta)
                .build().make();
    }


    public static ItemStack oneDollarBill() {
        return baseBill("one", 1.0, 101);
    }

    public static ItemStack fiveDollarBill() {
        return baseBill("five", 5.0, 102);

    }

    public static ItemStack tenDollarBill() {
        return baseBill("ten", 10.0, 103);

    }

    public static ItemStack twentyDollarBill() {
        return baseBill("twenty", 20.0, 104);
    }

    public static ItemStack fiftyDollarBill() {
        return baseBill("fifty", 50.0, 105);
    }

    public static ItemStack oneHundredDollarBill() {
        return baseBill("one hundred", 100.0, 106);
    }

    public static ItemStack baseCoin(final String type, final double value, final int cmd) {
        final ItemStack coin = new ItemStack(Material.PAPER);

        final ItemMeta meta = coin.getItemMeta();
        meta.setCustomModelData(cmd);
        meta.getPersistentDataContainer().set(VALUE_KEY, PersistentDataType.DOUBLE, value);
        coin.setItemMeta(meta);
        return  ItemCreator.of(CompMaterial.PAPER)
                .name(ItemUtil.bountifyCapitalized(type) + " Cent Coin")
                .meta(meta)
                .build().make();
    }


    public static ItemStack oneCentCoin() {
        return baseBill("one", 0.01, 101);
    }

    public static ItemStack fiveCentCoin() {
        return baseBill("five", 0.05, 102);

    }

    public static ItemStack tenCentCoin() {
        return baseBill("ten", 0.010, 103);

    }

    public static ItemStack twentyFiveCentCoin() {
        return baseBill("twenty", 0.025, 104);
    }

    public static boolean isCurrencyItem(final ItemStack item) {
        if (item == null) return false;
        if (!item.hasItemMeta()) return false;

        return item.getItemMeta().getPersistentDataContainer().has(VALUE_KEY, PersistentDataType.DOUBLE);
    }

    public static double getValueFromItem(final ItemStack economyItem) {
        if (!isCurrencyItem(economyItem)) return 0;

        return economyItem.getItemMeta().getPersistentDataContainer().get(VALUE_KEY, PersistentDataType.DOUBLE);
    }

    public static ItemStack getItemFromValue(final double value) {
        if (value == 100) {
            return oneHundredDollarBill();
        } else if (value == 50) {
            return fiftyDollarBill();
        } else if (value == 20) {
            return twentyDollarBill();
        } else if (value == 10) {
            return tenDollarBill();
        } else if (value == 5) {
            return fiveDollarBill();
        } else if (value == 1) {
            return oneDollarBill();
        } else if (value == 0.25) {
            return twentyFiveCentCoin();
        } else if (value == 0.10) {
            return tenCentCoin();
        } else if (value == 0.05) {
            return fiveCentCoin();
        } else if (value == 0.01) {
            return oneCentCoin();
        }
        return null;
    }
}
