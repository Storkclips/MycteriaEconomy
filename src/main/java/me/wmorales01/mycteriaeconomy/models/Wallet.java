package me.wmorales01.mycteriaeconomy.models;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.files.ConfigManager;
import me.wmorales01.mycteriaeconomy.inventories.WalletHolder;
import me.wmorales01.mycteriaeconomy.util.BalanceManager;
import me.wmorales01.mycteriaeconomy.util.Checker;
import me.wmorales01.mycteriaeconomy.util.Getter;
import me.wmorales01.mycteriaeconomy.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class Wallet {
    private UUID uuid;
    private double balance;
    private Inventory content;

    public Wallet() {
        uuid = UUID.randomUUID();
        balance = 0.0;
        this.content = Bukkit.createInventory(null, 36);
    }

    public Wallet(UUID uuid, double balance, Inventory content) {
        this.uuid = uuid;
        this.balance = balance;
        this.content = content;
    }

    public ItemStack getItemStack() {
        MycteriaEconomy plugin = MycteriaEconomy.getPlugin(MycteriaEconomy.class);
        ItemStack item = new ItemStack(ConfigManager.getWalletItem());
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.RESET + "Wallet");
        meta.setCustomModelData(101);
        List<String> lore = new ArrayList<String>();
        DecimalFormat format = new DecimalFormat("#.##");
        format.setRoundingMode(RoundingMode.CEILING);
        lore.add(StringUtil.formatColor("&a&oBalance: &l" + format.format(balance) + "$"));
        meta.setLore(lore);
        if (balance == 0)
            meta.setCustomModelData(101);
        else if (balance > 0 && balance < 1000)
            meta.setCustomModelData(102);
        else if (balance >= 1000)
            meta.setCustomModelData(103);

        NamespacedKey key = new NamespacedKey(plugin, "wallet_id");
        meta.getPersistentDataContainer().set(key, new UUIDDataContainer(), uuid);
        item.setItemMeta(meta);

        return item;
    }

    public Inventory getWalletGUI() {
        Inventory inventory = Bukkit.createInventory(new WalletHolder(), 36, "Wallet's Balance: ");

        if (content != null)
            inventory.setContents(content.getContents());

        return inventory;
    }

    public void increaseBalance(double increase) {
        for (ItemStack currencyItem : BalanceManager.getBalanceItems(increase))
            content.addItem(currencyItem);

        this.balance += increase;
    }

    public void discountBalance(double discount) {
        Map<Double, Integer> availableCurrencies = new TreeMap<>(Collections.reverseOrder());
        // Mapping current currencies
        for (ItemStack item : content) {
            if (item == null || item.getType().isAir())
                continue;

            int amount = item.getAmount();
            item = item.clone();
            item.setAmount(1);
            double value;
            if (Checker.isBill(item))
                value = Getter.getValueFromBill(item);
            else if (Checker.isCoin(item))
                value = Getter.getValueFromCoin(item);
            else
                value = 0;

            if (availableCurrencies.containsKey(value))
                availableCurrencies.put(value, availableCurrencies.get(value) + amount);
            else
                availableCurrencies.put(value, amount);
        }
        // Paying debt
        Iterator<Double> iterator = availableCurrencies.keySet().iterator();
        ArrayList<ItemStack> toRemove = new ArrayList<>();
        double discountCopy = discount;
        while (iterator.hasNext() && discountCopy > 0) {
            double value = iterator.next();
            int amount = availableCurrencies.get(value);
            for (int i = 0; i < amount; i++) {
                if (discountCopy <= 0)
                    break;

                toRemove.add(Getter.getCurrencyFromValue(value));
                discountCopy -= value;

                amount--;
                if (amount > 0)
                    availableCurrencies.put(value, amount);
                else
                    availableCurrencies.remove(value);
            }
        }
        if (discountCopy < 0) {
            double change = Math.abs(discountCopy);
            Iterator<ItemStack> currencyIterator = BalanceManager.getBalanceItems(change).iterator();
            for (int i = 0; i < content.getSize(); i++) {
                if (!currencyIterator.hasNext())
                    break;
                ItemStack walletItem = content.getItem(i);
                if (walletItem != null && !walletItem.getType().isAir())
                    continue;

                content.addItem(currencyIterator.next());
            }
        }
        this.balance -= discount;
        // Removing items from wallet
        for (ItemStack currencyItem : toRemove)
            content.removeItem(currencyItem);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Inventory getContent() {
        return content;
    }

    public void setContent(Inventory content) {
        this.content = content;
    }

    public static Wallet getByItemStack(ItemStack item) {
        MycteriaEconomy plugin = MycteriaEconomy.getPlugin(MycteriaEconomy.class);
        NamespacedKey key = new NamespacedKey(plugin, "wallet_id");
        if (item == null)
            return null;
        if (!item.hasItemMeta())
            return null;
        if (item.getItemMeta().getPersistentDataContainer() == null)
            return null;
        if (!item.getItemMeta().getPersistentDataContainer().has(key, new UUIDDataContainer()))
            return null;

        UUID itemUUID = item.getItemMeta().getPersistentDataContainer().get(key, new UUIDDataContainer());
        for (Wallet wallet : plugin.getWallets()) {
            UUID walletUUID = wallet.getUuid();
            if (!walletUUID.equals(itemUUID))
                continue;

            return wallet;
        }
        return null;
    }

    public boolean isSimilar(ItemStack item) {
        MycteriaEconomy plugin = MycteriaEconomy.getPlugin(MycteriaEconomy.class);
        NamespacedKey key = new NamespacedKey(plugin, "wallet_id");

        if (!item.hasItemMeta())
            return false;
        if (item.getItemMeta().getPersistentDataContainer() == null)
            return false;
        if (!item.getItemMeta().getPersistentDataContainer().has(key, new UUIDDataContainer()))
            return false;

        UUID itemUUID = item.getItemMeta().getPersistentDataContainer().get(key, new UUIDDataContainer());
        if (!itemUUID.equals(uuid))
            return false;

        return true;
    }
}
