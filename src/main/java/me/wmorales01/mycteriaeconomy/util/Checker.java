package me.wmorales01.mycteriaeconomy.util;

import me.wmorales01.mycteriaeconomy.models.EconomyItem;
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
        EconomyItem economyItem = new EconomyItem();
        if (item.isSimilar(economyItem.oneDollarBill()))
            return true;
        if (item.isSimilar(economyItem.fiveDollarBill()))
            return true;
        if (item.isSimilar(economyItem.tenDollarBill()))
            return true;
        if (item.isSimilar(economyItem.twentyDollarBill()))
            return true;
        if (item.isSimilar(economyItem.fiftyDollarBill()))
            return true;
        if (item.isSimilar(economyItem.oneHundredDollarBill()))
            return true;

        return false;
    }

    public static boolean isCoin(ItemStack item) {
        if (item == null)
            return false;
        EconomyItem economyItem = new EconomyItem();
        if (item.isSimilar(economyItem.oneCentCoin()))
            return true;
        if (item.isSimilar(economyItem.fiveCentCoin()))
            return true;
        if (item.isSimilar(economyItem.tenCentCoin()))
            return true;
        if (item.isSimilar(economyItem.twentyFiveCentCoin()))
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
