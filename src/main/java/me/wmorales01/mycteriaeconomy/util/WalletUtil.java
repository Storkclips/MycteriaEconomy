package me.wmorales01.mycteriaeconomy.util;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.UUIDDataContainer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class WalletUtil {
    protected static final MycteriaEconomy PLUGIN = MycteriaEconomy.getInstance();
    protected static final NamespacedKey WALLET_KEY = new NamespacedKey(PLUGIN, "wallet_uuid");
    protected static final UUIDDataContainer UUID_DATA_CONTAINER = new UUIDDataContainer();

    public static void addWalletUuidKey(ItemStack item, UUID uuid) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(WALLET_KEY, UUID_DATA_CONTAINER, uuid);
        item.setItemMeta(meta);
    }

    public static UUID getUuidFromItem(ItemStack item) {
        if (!isWallet(item)) return null;

        return item.getItemMeta().getPersistentDataContainer().get(WALLET_KEY, UUID_DATA_CONTAINER);
    }

    public static boolean isWallet(ItemStack item) {
        if (item == null) return false;
        if (!item.hasItemMeta()) return false;
        if (item.getType().isAir()) return false;

        return item.getItemMeta().getPersistentDataContainer().has(WALLET_KEY, UUID_DATA_CONTAINER);
    }
}
