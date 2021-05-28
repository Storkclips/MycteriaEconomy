package me.wmorales01.mycteriaeconomy.models;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.inventories.ATMHolder;

public class ATM {
	private Location location;
	
	public ATM(Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public static ATM getATM(Location location) {
		MycteriaEconomy plugin = MycteriaEconomy.getPlugin(MycteriaEconomy.class);
		
		for (ATM atm : plugin.getATMs()) {
			if (!atm.getLocation().equals(location))
				continue;
			
			return atm;
		}
		
		return null;
	}

	public static ItemStack getATMItem() {
		ItemStack atm = new ItemStack(Material.DISPENSER);
		ItemMeta meta = atm.getItemMeta();

		meta.setDisplayName(ChatColor.YELLOW + "ATM");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.ITALIC + "" + ChatColor.GOLD + "Place it to install a new ATM");
		meta.setLore(lore);
		atm.setItemMeta(meta);

		return atm;
	}
	
	public static Inventory getWithdrawATMGUI(double balance) {
		Inventory inventory = Bukkit.createInventory(new ATMHolder(), 36, "ATM");

		buildATMGUI(inventory, true, balance);

		return inventory;
	}

	private static void setATMItem(ItemStack item, String name, String loreLine, Inventory inventory, int slot) {
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
		meta.setLore(lore);
		item.setItemMeta(meta);

		inventory.setItem(slot, item);
	}

	private static void buildATMGUI(Inventory inventory, boolean isWithdrawOperation, double balance) {
		EconomyItems ecoItems = new EconomyItems();
		if (isWithdrawOperation) {
			setATMItem(ecoItems.oneDollarBill(), "&r&aWithdraw 1$", "&6Click to withdraw.", inventory, 2);
			setATMItem(ecoItems.fiveDollarBill(), "&r&aWithdraw 5$", "&6Click to withdraw.", inventory, 3);
			setATMItem(ecoItems.tenDollarBill(), "&r&aWithdraw 10$", "&6Click to withdraw.", inventory, 4);
			setATMItem(ecoItems.twentyDollarBill(), "&r&aWithdraw 20$", "&6Click to withdraw.", inventory, 5);
			setATMItem(ecoItems.fiftyDollarBill(), "&r&aWithdraw 50$", "&6Click to withdraw.", inventory, 6);
			setATMItem(ecoItems.oneHundredDollarBill(), "&r&aWithdraw 100$", "&6Click to withdraw.", inventory, 13);
			setATMItem(ecoItems.oneCentCoin(), "&r&aWithdraw 0.01$", "&6Click to withdraw.", inventory, 11);
			setATMItem(ecoItems.fiveCentCoin(), "&r&aWithdraw 0.05$", "&6Click to withdraw.", inventory, 12);
			setATMItem(ecoItems.tenCentCoin(), "&r&aWithdraw 0.10$", "&6Click to withdraw.", inventory, 14);
			setATMItem(ecoItems.twentyFiveCentCoin(), "&r&aWithdraw 0.25$", "&6Click to withdraw.", inventory, 15);
		} else {
			setATMItem(ecoItems.oneDollarBill(), "&r&aDeposit 1$", "&6Click to deposit.", inventory, 2);
			setATMItem(ecoItems.fiveDollarBill(), "&r&aDeposit 5$", "&6Click to deposit.", inventory, 3);
			setATMItem(ecoItems.tenDollarBill(), "&r&aDeposit 10$", "&6Click to deposit.", inventory, 4);
			setATMItem(ecoItems.twentyDollarBill(), "&r&aDeposit 20$", "&6Click to deposit.", inventory, 5);
			setATMItem(ecoItems.fiftyDollarBill(), "&r&aDeposit 50$", "&6Click to deposit.", inventory, 6);
			setATMItem(ecoItems.oneHundredDollarBill(), "&r&aDeposit 100$", "&6Click to deposit.", inventory, 13);
			setATMItem(ecoItems.oneCentCoin(), "&r&aDeposit 0.01$", "&6Click to deposit.", inventory, 11);
			setATMItem(ecoItems.fiveCentCoin(), "&r&aDeposit 0.05$", "&6Click to deposit.", inventory, 12);
			setATMItem(ecoItems.tenCentCoin(), "&r&aDeposit 0.10$", "&6Click to deposit.", inventory, 14);
			setATMItem(ecoItems.twentyFiveCentCoin(), "&r&aDeposit 0.25$", "&6Click to deposit.", inventory, 15);
		}
		ItemStack info = new ItemStack(Material.PAPER);
		setATMItem(info, "&r&aDeposit Info",
				"&6You can click a bill or coin from your inventory to deposit it into your account.", inventory, 21);

		DecimalFormat format = new DecimalFormat("###.##");
		setATMItem(new ItemStack(Material.SUNFLOWER), "&r&eBalance:", "&6&l" + format.format(balance) + "$", inventory,
				22);
		setATMItem(new ItemStack(Material.BLAZE_POWDER), "&r&cTransfaction Fee:", "&c&l1$", inventory, 23);
	}
}
