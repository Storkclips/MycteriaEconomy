package me.wmorales01.mycteriaeconomy.models;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class CurrencyItem {
    // Namespacedkey that will be used to store the economic value of each item on its persistent data container
    protected static final NamespacedKey VALUE_KEY = new NamespacedKey(MycteriaEconomy.getInstance(), "value");

    public static List<ItemStack> getEconomyItems() {
        return new ArrayList<ItemStack>() {{
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
        }};
    }

    public static ItemStack oneDollarBill() {
        ItemStack bill = new ItemStack(Material.PAPER);
        ItemMeta meta = bill.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "One Dollar Bill");
        meta.setCustomModelData(101);
        meta.getPersistentDataContainer().set(VALUE_KEY, PersistentDataType.DOUBLE, 1.0);
        bill.setItemMeta(meta);
        return bill;
    }

    public static ItemStack fiveDollarBill() {
        ItemStack bill = new ItemStack(Material.PAPER);
        ItemMeta meta = bill.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Five Dollar Bill");
        meta.setCustomModelData(102);
        meta.getPersistentDataContainer().set(VALUE_KEY, PersistentDataType.DOUBLE, 5.0);
        bill.setItemMeta(meta);
        return bill;
    }

    public static ItemStack tenDollarBill() {
        ItemStack bill = new ItemStack(Material.PAPER);
        ItemMeta meta = bill.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Ten Dollar Bill");
        meta.setCustomModelData(103);
        meta.getPersistentDataContainer().set(VALUE_KEY, PersistentDataType.DOUBLE, 10.0);
        bill.setItemMeta(meta);
        return bill;
    }

    public static ItemStack twentyDollarBill() {
        ItemStack bill = new ItemStack(Material.PAPER);
        ItemMeta meta = bill.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Twenty Dollar Bill");
        meta.setCustomModelData(104);
        meta.getPersistentDataContainer().set(VALUE_KEY, PersistentDataType.DOUBLE, 20.0);
        bill.setItemMeta(meta);
        return bill;
    }

    public static ItemStack fiftyDollarBill() {
        ItemStack bill = new ItemStack(Material.PAPER);
        ItemMeta meta = bill.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Fifty Dollar Bill");
        meta.setCustomModelData(105);
        meta.getPersistentDataContainer().set(VALUE_KEY, PersistentDataType.DOUBLE, 50.0);
        bill.setItemMeta(meta);
        return bill;
    }

    public static ItemStack oneHundredDollarBill() {
        ItemStack bill = new ItemStack(Material.PAPER);
        ItemMeta meta = bill.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "One Hundred Dollar Bill");
        meta.setCustomModelData(106);
        meta.getPersistentDataContainer().set(VALUE_KEY, PersistentDataType.DOUBLE, 100.0);
        bill.setItemMeta(meta);
        return bill;
    }

    public static ItemStack oneCentCoin() {
        ItemStack coin = new ItemStack(Material.IRON_NUGGET);
        ItemMeta meta = coin.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "One Cent Coin");
        meta.setCustomModelData(101);
        meta.getPersistentDataContainer().set(VALUE_KEY, PersistentDataType.DOUBLE, 0.01);
        coin.setItemMeta(meta);
        return coin;
    }

    public static ItemStack fiveCentCoin() {
        ItemStack coin = new ItemStack(Material.IRON_NUGGET);
        ItemMeta meta = coin.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Five Cent Coin");
        meta.setCustomModelData(102);
        meta.getPersistentDataContainer().set(VALUE_KEY, PersistentDataType.DOUBLE, 0.05);
        coin.setItemMeta(meta);
        return coin;
    }

    public static ItemStack tenCentCoin() {
        ItemStack coin = new ItemStack(Material.IRON_NUGGET);
        ItemMeta meta = coin.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Ten Cent Coin");
        meta.setCustomModelData(103);
        meta.getPersistentDataContainer().set(VALUE_KEY, PersistentDataType.DOUBLE, 0.10);
        coin.setItemMeta(meta);
        return coin;
    }

    public static ItemStack twentyFiveCentCoin() {
        ItemStack coin = new ItemStack(Material.IRON_NUGGET);
        ItemMeta meta = coin.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Twenty Five Cent Coin");
        meta.setCustomModelData(104);
        meta.getPersistentDataContainer().set(VALUE_KEY, PersistentDataType.DOUBLE, 0.25);
        coin.setItemMeta(meta);
        return coin;
    }

    public static boolean isCurrencyItem(ItemStack item) {
        if (item == null) return false;
        if (!item.hasItemMeta()) return false;

        return item.getItemMeta().getPersistentDataContainer().has(VALUE_KEY, PersistentDataType.DOUBLE);
    }

    public static double getValueFromItem(ItemStack economyItem) {
        if (!isCurrencyItem(economyItem)) return 0;

        return economyItem.getItemMeta().getPersistentDataContainer().get(VALUE_KEY, PersistentDataType.DOUBLE);
    }

    public static ItemStack getItemFromValue(double value) {
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
