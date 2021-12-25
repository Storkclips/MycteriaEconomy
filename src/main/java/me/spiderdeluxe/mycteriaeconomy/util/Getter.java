package me.spiderdeluxe.mycteriaeconomy.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Getter {

    public static int getValueFromBill(final ItemStack bill) {
        final ItemMeta meta = bill.getItemMeta();
        if (!meta.hasCustomModelData())
            return 0;
        final int modelData = meta.getCustomModelData();
        switch (modelData) {
            case 101:
                return bill.getAmount();

            case 102:
                return 5 * bill.getAmount();

            case 103:
                return 10 * bill.getAmount();

            case 104:
                return 20 * bill.getAmount();

            case 105:
                return 50 * bill.getAmount();

            case 106:
                return 100 * bill.getAmount();

            default:
                return 0;
        }
    }

    public static double getValueFromCoin(final ItemStack coin) {
        final ItemMeta meta = coin.getItemMeta();
        if (!meta.hasCustomModelData())
            return 0;
        final int modelData = meta.getCustomModelData();

        switch (modelData) {
            case 101:
                return 0.01 * coin.getAmount();

            case 102:
                return 0.05 * coin.getAmount();

            case 103:
                return 0.10 * coin.getAmount();

            case 104:
                return 0.25 * coin.getAmount();

            default:
                return 0;

        }
    }


}
