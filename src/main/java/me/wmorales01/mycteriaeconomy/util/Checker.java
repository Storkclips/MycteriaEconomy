package me.wmorales01.mycteriaeconomy.util;

import me.wmorales01.mycteriaeconomy.models.CurrencyItem;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class Checker {

    public static boolean isValidAmount(CommandSender sender, int amount) {
        if (amount <= 0) {
            Messager.sendMessage(sender, "&cYou must enter a number higher than 0.");
            return false;
        }

        return true;
    }

    public static boolean isBill(ItemStack item) {
        if (item == null)
            return false;
        CurrencyItem currencyItem = new CurrencyItem();
        if (item.isSimilar(currencyItem.oneDollarBill()))
            return true;
        if (item.isSimilar(currencyItem.fiveDollarBill()))
            return true;
        if (item.isSimilar(currencyItem.tenDollarBill()))
            return true;
        if (item.isSimilar(currencyItem.twentyDollarBill()))
            return true;
        if (item.isSimilar(currencyItem.fiftyDollarBill()))
            return true;
        if (item.isSimilar(currencyItem.oneHundredDollarBill()))
            return true;

        return false;
    }

    public static boolean isCoin(ItemStack item) {
        if (item == null)
            return false;
        CurrencyItem currencyItem = new CurrencyItem();
        if (item.isSimilar(currencyItem.oneCentCoin()))
            return true;
        if (item.isSimilar(currencyItem.fiveCentCoin()))
            return true;
        if (item.isSimilar(currencyItem.tenCentCoin()))
            return true;
        if (item.isSimilar(currencyItem.twentyFiveCentCoin()))
            return true;

        return false;
    }

    public static boolean isFrame(ItemStack item) {
        if (item == null)
            return false;
        if (!item.hasItemMeta())
            return false;
        if (item.getItemMeta().getDisplayName().equals(" "))
            return true;

        return false;
    }
}
