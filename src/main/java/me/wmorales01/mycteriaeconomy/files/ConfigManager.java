package me.wmorales01.mycteriaeconomy.files;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.util.LogUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private static final MycteriaEconomy PLUGIN = MycteriaEconomy.getInstance();

    public static Material getWalletItem() {
        String materialName = getConfig().getString("wallet-item");
        if (materialName == null) {
            LogUtil.sendWarnLog("Wallet item not provided. Using default item.");
            return Material.LEATHER;
        }
        Material material = Material.matchMaterial(materialName);
        if (material == null) return Material.LEATHER;

        return material;
    }

    public static double getAtmTranstacionFee() {
        return getConfig().getDouble("atm-transaction-fee");
    }

    public static double getNpcLookDistance() {
        return getConfig().getDouble("npc-look-distance");
    }

    public static int getStockDiscountAmount() {
        return PLUGIN.getConfig().getInt("npcshop-stock-amount-discount");
    }

    private static FileConfiguration getConfig() {
        return PLUGIN.getConfig();
    }
}
