package me.spiderdeluxe.mycteriaeconomy.models;

import me.spiderdeluxe.mycteriaeconomy.MycteriaEconomyPlugin;
import me.spiderdeluxe.mycteriaeconomy.inventories.WalletHolder;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import me.spiderdeluxe.mycteriaeconomy.util.BalanceUtil;
import me.spiderdeluxe.mycteriaeconomy.util.StringUtil;
import me.spiderdeluxe.mycteriaeconomy.util.UUIDDataContainer;
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
    private static final MycteriaEconomyPlugin PLUGIN = MycteriaEconomyPlugin.getInstance();
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
    public Wallet(final UUID uuid) {
        this.uuid = uuid;
        balance = 0.0;
        this.content = Bukkit.createInventory(new WalletHolder(this), 36, "Wallet");
    }

    public Wallet(final UUID uuid, final double balance, final ItemStack[] content) {
        this.uuid = uuid;
        this.balance = balance;
        this.content = Bukkit.createInventory(new WalletHolder(this), 36, "Wallet");
        this.content.setContents(content);
    }

    public static Wallet fromItemStack(final ItemStack item) {
        final UUID walletUuid = getUuidFromItem(item);
        if (walletUuid == null) return null;

        return MycteriaEconomyPlugin.getInstance().getWalletManager().loadWallet(walletUuid);
    }

    public static UUID getUuidFromItem(final ItemStack item) {
        if (!isWallet(item)) return null;

        return item.getItemMeta().getPersistentDataContainer().get(WALLET_KEY, UUID_DATA_CONTAINER);
    }

    public static ItemStack findItemWalletInInv(final Inventory inventory) {
        for(final ItemStack item : inventory.getContents()) {
            if(isWallet(item))
                return item;
        }
        return null;
    }

    public static int findSlotWalletInInv(final Inventory inventory) {
        for(int i = 0; i <= inventory.getSize(); i++) {
            if(isWallet(inventory.getItem(i)))
                return i;
        }
        return 0;
    }

    public static boolean isWallet(final ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        return item.getItemMeta().getPersistentDataContainer().has(WALLET_KEY, UUID_DATA_CONTAINER);
    }

    public void saveWalletData() {
        MycteriaEconomyPlugin.getInstance().getWalletManager().saveWallet(this);
    }

    public void computeWalletBalance() {
        this.balance = BalanceUtil.computeInventoryBalance(content);
    }

    // Returns if the passed waller is similar to the current instance
    public boolean isSimilar(final ItemStack wallet) {
        final UUID walletUuid = getUuidFromItem(wallet);
        if (walletUuid == null) return false;

        return walletUuid.equals(uuid);
    }

    public  ItemStack getItemStack() {
        final ItemStack item = new ItemStack(Settings.General.WALLET_ITEM);
        final ItemMeta meta = item.getItemMeta();
        final List<String> lore = new ArrayList<>();
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

    private void addWalletUuidKey(final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(WALLET_KEY, UUID_DATA_CONTAINER, uuid);
        item.setItemMeta(meta);
    }

    public void increaseWalletBalance(final double increase) {
        for (final ItemStack currencyItem : BalanceUtil.balanceToCurrency(increase)) {
            content.addItem(currencyItem);
        }
        this.balance += increase;
        saveWalletData();
    }

    public void decreaseBalance(final double discount) {
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
