package me.wmorales01.mycteriaeconomy.util;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import me.wmorales01.mycteriaeconomy.models.EconomyItems;

public class Checker {
	
	public static boolean isValidAmount(CommandSender sender, int amount) {
		if (amount <= 0) {
			Messager.sendMessage(sender, "&cYou must enter a number higher than 0.");
			return false;
		}
		
		return true;
	}
	
	public static boolean isBill(ItemStack item) {
		if (item == null)
			return false;
		EconomyItems economyItems = new EconomyItems();
		if (item.isSimilar(economyItems.oneDollarBill()))
			return true;
		if (item.isSimilar(economyItems.fiveDollarBill()))
			return true;
		if (item.isSimilar(economyItems.tenDollarBill()))
			return true;
		if (item.isSimilar(economyItems.twentyDollarBill()))
			return true;
		if (item.isSimilar(economyItems.fiftyDollarBill()))
			return true;
		if (item.isSimilar(economyItems.oneHundredDollarBill()))
			return true;
		
		return false;
	}
	
	public static boolean isCoin(ItemStack item) {
		if (item == null)
			return false;
		EconomyItems economyItems = new EconomyItems();
		if (item.isSimilar(economyItems.oneCentCoin()))
			return true;
		if (item.isSimilar(economyItems.fiveCentCoin()))
			return true;
		if (item.isSimilar(economyItems.tenCentCoin()))
			return true;
		if (item.isSimilar(economyItems.twentyFiveCentCoin()))
			return true;
		
		return false;
	}
	
	public static boolean isFrame(ItemStack item) {
		if (item == null)
			return false;
		if (!item.hasItemMeta())
			return false;
		if (item.getItemMeta().getDisplayName().equals(" "))
			return true;
		
		return false;
	}
}
