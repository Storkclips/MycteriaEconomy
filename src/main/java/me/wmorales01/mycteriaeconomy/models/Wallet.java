package me.wmorales01.mycteriaeconomy.models;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.files.ConfigManager;
import me.wmorales01.mycteriaeconomy.inventories.WalletHolder;
import me.wmorales01.mycteriaeconomy.util.BalanceUtil;
import me.wmorales01.mycteriaeconomy.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Wallet {
    private static final MycteriaEconomy PLUGIN = MycteriaEconomy.getInstance();
    private static final NamespacedKey WALLET_KEY = new NamespacedKey(PLUGIN, "wallet_uuid");
    private static final UUIDDataContainer UUID_DATA_CONTAINER = new UUIDDataContainer();

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
        UUID walletUuid = getUuidFromItem(item);
        if (walletUuid == null) return null;

        return MycteriaEconomy.getInstance().getWalletManager().loadWallet(walletUuid);
    }

    public static UUID getUuidFromItem(ItemStack item) {
        if (!isWallet(item)) return null;

        return item.getItemMeta().getPersistentDataContainer().get(WALLET_KEY, UUID_DATA_CONTAINER);
    }

    public static boolean isWallet(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        return item.getItemMeta().getPersistentDataContainer().has(WALLET_KEY, UUID_DATA_CONTAINER);
    }

    public void saveWalletData() {
        MycteriaEconomy.getInstance().getWalletManager().saveWallet(this);
    }

    public void computeWalletBalance() {
        this.balance = BalanceUtil.computeInventoryBalance(content);
    }

    // Returns if the passed waller is similar to the current instance
    public boolean isSimilar(ItemStack wallet) {
        UUID walletUuid = getUuidFromItem(wallet);
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
        addWalletUuidKey(item);
        return item;
    }

    private void addWalletUuidKey(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(WALLET_KEY, UUID_DATA_CONTAINER, uuid);
        item.setItemMeta(meta);
    }

    public void increaseWalletBalance(double increase) {
        for (ItemStack currencyItem : BalanceUtil.balanceToCurrency(increase)) {
            content.addItem(currencyItem);
        }
        this.balance += increase;
        saveWalletData();
    }

    public void decreaseBalance(double discount) {
        this.balance -= discount;
        BalanceUtil.removeBalance(content, discount);
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
