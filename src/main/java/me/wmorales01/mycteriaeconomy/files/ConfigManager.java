package me.wmorales01.mycteriaeconomy.files;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private static final MycteriaEconomy PLUGIN = MycteriaEconomy.getInstance();

    public static double getAtmTranstacionFee() {
        return getConfig().getDouble("atm-transaction-fee");
    }

    public static int getStockDiscountAmount() {
        return PLUGIN.getConfig().getInt("npcshop-stock-amount-discount");
    }

    public static Material getCurrencyItem() {
        Material material = Material.matchMaterial(PLUGIN.getConfig().getString("currency-item"));
        if (material == null)
            material = Material.GOLD_NUGGET;

        return material;
    }

    public static Material getWalletItem() {
        Material material = Material.matchMaterial(PLUGIN.getConfig().getString("wallet-item"));
        if (material == null)
            material = Material.LEATHER;

        return material;
    }

    private static FileConfiguration getConfig() {
        return PLUGIN.getConfig();
    }
}
