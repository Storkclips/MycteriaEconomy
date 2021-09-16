package me.wmorales01.mycteriaeconomy.util;

import me.wmorales01.mycteriaeconomy.models.CurrencyItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BalanceUtil {

    /**
     * Gives the passed balance to the passed player
     *
     * @param player  Player that will receive the balance's currency items.
     * @param balance Balance that will be converted to currency items.
     */
    public static void giveBalance(Player player, double balance) {
        InventoryUtil.giveItems(player, balanceToCurrency(balance));
    }

    /**
     * Removes the passed balance from the passed inventory in the form of currency items.
     *
     * @param inventory Inventory where the balance will be removed from.
     * @param balance   Balance that will be converted to currency and removed from the inventory.
     */
    public static void removeBalance(Inventory inventory, double balance) {
        Map<Double, Integer> availableCurrencies = new TreeMap<>(Collections.reverseOrder());
        // Mapping current currencies in descending order
        for (ItemStack economyItem : inventory.getContents()) {
            if (!CurrencyItem.isCurrencyItem(economyItem)) continue;

            int itemAmount = economyItem.getAmount();
            double economyItemvalue = CurrencyItem.getValueFromItem(economyItem);
            if (availableCurrencies.containsKey(economyItemvalue)) {
                int availableAmount = availableCurrencies.get(economyItemvalue);
                availableCurrencies.put(economyItemvalue, availableAmount + itemAmount);
            } else {
                availableCurrencies.put(economyItemvalue, itemAmount);
            }
        }
        // Iteration through all the available economy item values from the wallet inventory in descending order
        // and disconting it from the passed balanceDiscuount until it is lower or equal to 0
        Iterator<Double> iterator = availableCurrencies.keySet().iterator();
        ArrayList<ItemStack> spentEconomyItems = new ArrayList<>();
        while (iterator.hasNext() && balance > 0) {
            double economyItemValue = iterator.next();
            int availableAmount = availableCurrencies.get(economyItemValue);
            while (availableAmount > 0) {
                if (balance <= 0) break;

                spentEconomyItems.add(CurrencyItem.getItemFromValue(economyItemValue));
                balance -= economyItemValue;
                availableAmount--;
                if (availableAmount > 0) {
                    availableCurrencies.put(economyItemValue, availableAmount);
                } else {
                    availableCurrencies.remove(economyItemValue);
                }
            }
        }
        // If the discount is lower than 0 that means that the transaction ended with a remaning change
        // Get the change items from the BalanceUtil and add them to the inventory
        if (balance < 0) {
            double change = -balance;
            List<ItemStack> changeItems = BalanceUtil.balanceToCurrency(change);
            for (ItemStack changeItem : changeItems) {
                inventory.addItem(changeItem);
            }
        }
        // Removing items from inventory
        for (ItemStack currencyItem : spentEconomyItems) {
            inventory.removeItem(currencyItem);
        }
    }

    public static double computeInventoryBalance(Inventory inventory) {
        ItemStack[] inventoryContents = inventory.getContents();
        double balance = 0.0;
        for (ItemStack item : inventoryContents) {
            if (item == null) continue;
            if (Checker.isBill(item)) {
                balance += Getter.getValueFromBill(item);
            } else if (Checker.isCoin(item)) {
                balance += Getter.getValueFromCoin(item);
            }
        }
        return balance;
    }

    // Returns the passed balance in the form of currency items
    // Ex. If passed a balance of 60, it will return a 50 and 10 dollar bills
    public static List<ItemStack> balanceToCurrency(double balance) {
        List<ItemStack> balanceItems = new ArrayList<>();
        while (balance > 0) {
            if (balance >= 100) {
                balanceItems.add(CurrencyItem.oneHundredDollarBill());
                balance -= 100;
            } else if (balance >= 50) {
                balanceItems.add(CurrencyItem.fiftyDollarBill());
                balance -= 50;
            } else if (balance >= 20) {
                balanceItems.add(CurrencyItem.twentyDollarBill());
                balance -= 20;
            } else if (balance >= 10) {
                balanceItems.add(CurrencyItem.tenDollarBill());
                balance -= 10;
            } else if (balance >= 5) {
                balanceItems.add(CurrencyItem.fiveDollarBill());
                balance -= 5;
            } else if (balance >= 1) {
                balanceItems.add(CurrencyItem.oneDollarBill());
                balance -= 1;
            } else if (balance >= 0.25) {
                balanceItems.add(CurrencyItem.twentyFiveCentCoin());
                balance -= 0.5;
            } else if (balance >= 0.10) {
                balanceItems.add(CurrencyItem.tenCentCoin());
                balance -= 0.10;
            } else if (balance >= 0.05) {
                balanceItems.add(CurrencyItem.fiveCentCoin());
                balance -= 0.05;
            } else if (balance >= 0.01) {
                balanceItems.add(CurrencyItem.oneCentCoin());
                balance -= 0.01;
            } else {
                balance = 0;
            }
        }
        return balanceItems;
    }
}
