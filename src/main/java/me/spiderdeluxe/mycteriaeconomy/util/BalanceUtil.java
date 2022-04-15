package me.spiderdeluxe.mycteriaeconomy.util;

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
    public static void giveBalance(final Player player, final double balance) {
        InventoryUtil.giveItems(player, balanceToCurrency(balance));
    }


    /**
     * Removes the passed balance from the passed inventory in the form of currency items.
     *
     * @param inventory Inventory where the balance will be removed from.
     * @param balance   Balance that will be converted to currency and removed from the inventory.
     */
    public static void removeBalance(final Inventory inventory, double balance) {
        final Map<Double, Integer> availableCurrencies = new TreeMap<>(getAvailableCurrencies(inventory));
        // Iteration through all the available economy item values from the wallet inventory in descending order
        // and disconting it from the passed balanceDiscount until it is lower or equal to 0

        final Map<Double, Integer> tempCurrencies = new TreeMap<>(availableCurrencies);
        final Set<Double> currencies = availableCurrencies.keySet();
        final Iterator<Double> iterator = currencies.iterator();

        final ArrayList<ItemStack> spentEconomyItems = new ArrayList<>();

        while (balance >= 1) {
            final double economyItemValue = iterator.next();
            int availableAmount = availableCurrencies.get(economyItemValue);
            while (availableAmount > 0) {
                if (balance <= 0) break;

                spentEconomyItems.add(CurrencyItem.getItemFromValue(economyItemValue));
                balance -= economyItemValue;
                availableAmount--;
                if (availableAmount > 0) {
                    tempCurrencies.put(economyItemValue, availableAmount);
                } else {
                    tempCurrencies.remove(economyItemValue);
                }
            }
        }
        // If the discount is lower than 0 that means that the transaction ended with a remaning change
        // Get the change items from the BalanceUtil and add them to the inventory
        if (balance < 0) {
            final double change = -balance;
            final List<ItemStack> changeItems = BalanceUtil.balanceToCurrency(change);
            for (final ItemStack changeItem : changeItems) {
                inventory.addItem(changeItem);
            }
        }


        // Removing items from inventory
        for (final ItemStack currencyItem : spentEconomyItems) {
            final ItemStack removeItem = Checker.findSimiliarInInv(inventory, currencyItem);
            if (removeItem != null) {
                for (int i = 0; i < currencyItem.getAmount(); i++)
                    removeItem.setAmount(removeItem.getAmount() - 1);
            }
        }

    }


    /**
     * Obtain available currencies in a inventory
     *
     * @param inventory the inventory
     * @return
     */
    public static Map<Double, Integer> getAvailableCurrencies(final Inventory inventory) {
        final Map<Double, Integer> availableCurrencies = new TreeMap<>(Collections.reverseOrder());
        // Mapping current currencies in descending order
        for (final ItemStack economyItem : inventory.getContents()) {
            if (!CurrencyItem.isCurrencyItem(economyItem)) continue;

            final int itemAmount = economyItem.getAmount();
            final double economyItemvalue = CurrencyItem.getValueFromItem(economyItem);
            if (availableCurrencies.containsKey(economyItemvalue)) {
                final int availableAmount = availableCurrencies.get(economyItemvalue);
                availableCurrencies.put(economyItemvalue, availableAmount + itemAmount);
            } else {
                availableCurrencies.put(economyItemvalue, itemAmount);
            }
        }
        return availableCurrencies;
    }


    public static double computeInventoryBalance(final Inventory inventory) {
        final ItemStack[] inventoryContents = inventory.getContents();
        double balance = 0.0;
        for (final ItemStack item : inventoryContents) {
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
        final List<ItemStack> balanceItems = new ArrayList<>();
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
