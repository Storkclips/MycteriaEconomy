package me.wmorales01.mycteriaeconomy.files;

import org.bukkit.Material;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;

public class ConfigManager {
	private static MycteriaEconomy PLUGIN = MycteriaEconomy.getPlugin(MycteriaEconomy.class);
	
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
}
