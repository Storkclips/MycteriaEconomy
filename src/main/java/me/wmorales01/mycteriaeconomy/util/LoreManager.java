package me.wmorales01.mycteriaeconomy.util;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LoreManager {

	public static int getBalance(String number) {
		String[] splitBalance = number.split(":");

		String stringBalance = ChatColor.stripColor(splitBalance[1]).replace("$", "").trim();
		int balance = Parser.getNumber(null, stringBalance);

		return balance;
	}
	
	public static void setBalanceLore(ItemStack item, double balance) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.clear();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a&oBalance: &l" + balance + "$"));
		meta.setLore(lore);
		item.setItemMeta(meta);
	}
}
