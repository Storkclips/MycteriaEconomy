package me.wmorales01.mycteriaeconomy.models;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.files.ConfigManager;
import me.wmorales01.mycteriaeconomy.inventories.WalletHolder;
import me.wmorales01.mycteriaeconomy.util.BalanceUtil;
import me.wmorales01.mycteriaeconomy.util.StringUtil;
import me.wmorales01.mycteriaeconomy.util.WalletUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Wallet {
    private final UUID uuid;
    private final Inventory content;
    private double balance;

    public Wallet() {
        this.uuid = UUID.randomUUID();
        this.balance = 0.0;
        this.content = Bukkit.createInventory(new WalletHolder(this), 36, "Wallet");
    }

    // Used when a wallet file isn't found
    public Wallet(UUID uuid) {
        this.uuid = uuid;
        balance = 0.0;
        this.content = Bukkit.createInventory(new WalletHolder(this), 36, "Wallet");
    }

    public Wallet(UUID uuid, double balance, ItemStack[] content) {
        this.uuid = uuid;
        this.balance = balance;
        this.content = Bukkit.createInventory(new WalletHolder(this), 36, "Wallet");
        this.content.setContents(content);
    }

    public static Wallet fromItemStack(ItemStack item) {
        UUID walletUuid = WalletUtil.getUuidFromItem(item);
        if (walletUuid == null) return null;

        return MycteriaEconomy.getInstance().getWalletManager().loadWallet(walletUuid);
    }

    public void saveWalletData() {
        MycteriaEconomy.getInstance().getWalletManager().saveWallet(this);
    }

    public void computeWalletBalance() {
        double balance = 0;
        for (ItemStack item : content.getContents()) {
            if (!EconomyItem.isEconomyItem(item)) continue;

            balance += EconomyItem.getValueFromItem(item);
        }
        this.balance = balance;
    }

    // Returns if the passed waller is similar to the current instance
    public boolean isSimilar(ItemStack wallet) {
        UUID walletUuid = WalletUtil.getUuidFromItem(wallet);
        if (walletUuid == null) return false;

        return walletUuid.equals(uuid);
    }

    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(ConfigManager.getWalletItem());
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.RESET + "Wallet");
        lore.add(StringUtil.formatColor("&a&oBalance: &l$" + StringUtil.roundNumber(balance, 2)));
        meta.setLore(lore);
        if (balance == 0) {
            meta.setCustomModelData(101);
        } else if (balance < 1000) {
            meta.setCustomModelData(102);
        } else if (balance >= 1000) {
            meta.setCustomModelData(103);
        }
        item.setItemMeta(meta);
        WalletUtil.addWalletUuidKey(item, uuid);
        return item;
    }

    public void increaseWalletBalance(double increase) {
        for (ItemStack currencyItem : BalanceUtil.balanceToCurrency(increase)) {
            content.addItem(currencyItem);
        }
        this.balance += increase;
        saveWalletData();
    }

    public void discountBalance(double discount) {
        Map<Double, Integer> availableCurrencies = new TreeMap<>(Collections.reverseOrder());
        // Mapping current currencies in descending order
        for (ItemStack economyItem : content.getContents()) {
            if (!EconomyItem.isEconomyItem(economyItem)) continue;

            int itemAmount = economyItem.getAmount();
            double economyItemvalue = EconomyItem.getValueFromItem(economyItem);
            if (availableCurrencies.containsKey(economyItemvalue)) {
                int availableAmount = availableCurrencies.get(economyItemvalue);
                availableCurrencies.put(economyItemvalue, availableAmount + itemAmount);
            } else {
                availableCurrencies.put(economyItemvalue, itemAmount);
            }
        }
        this.balance -= discount;
        // Iteration through all the available economy item values from the wallet inventory in descending order
        // and disconting it from the passed balanceDiscuount until it it lower or equal to 0
        Iterator<Double> iterator = availableCurrencies.keySet().iterator();
        ArrayList<ItemStack> spentEconomyItems = new ArrayList<>();
        while (iterator.hasNext() && discount > 0) {
            double economyItemValue = iterator.next();
            int availableAmount = availableCurrencies.get(economyItemValue);
            while (availableAmount > 0) {
                if (discount <= 0) break;

                spentEconomyItems.add(EconomyItem.getItemFromValue(economyItemValue));
                discount -= economyItemValue;
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
        if (discount < 0) {
            double change = -discount;
            List<ItemStack> changeItems = BalanceUtil.balanceToCurrency(change);
            for (ItemStack changeItem : changeItems) {
                content.addItem(changeItem);
            }
        }
        // Removing items from wallet
        for (ItemStack currencyItem : spentEconomyItems) {
            content.removeItem(currencyItem);
        }
        saveWalletData();
    }

    public UUID getUuid() {
        return uuid;
    }

    public Inventory getGUI() {
        return content;
    }

    public double getBalance() {
        return balance;
    }
}
