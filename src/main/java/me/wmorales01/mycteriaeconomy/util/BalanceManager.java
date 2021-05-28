package me.wmorales01.mycteriaeconomy.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import me.wmorales01.mycteriaeconomy.files.ConfigManager;
import me.wmorales01.mycteriaeconomy.models.EconomyItems;

public class BalanceManager {

	public static void giveBalance(Player player, double balance) {
		PlayerInventory inventory = player.getInventory();

		List<ItemStack> balanceItems = getBalanceItems(balance);
		for (ItemStack item : balanceItems)
			inventory.addItem(item);
	}

	public static double getBalanceFromInventory(Inventory inventory) {
		ItemStack[] inventoryContents = inventory.getContents();
		double balance = 0.0;
		for (ItemStack item : inventoryContents) {
			if (item == null)
				continue;
			if (Checker.isBill(item))
				balance += Getter.getValueFromBill(item);
			else if (Checker.isCoin(item))
				balance += Getter.getValueFromCoin(item);
		}
		return balance;
	}

	public static void updateWalletBalance(Inventory inventory, int initialBalance, int finalBalance) {
		ItemStack wallet = null;
		for (ItemStack item : inventory.getContents()) {
			if (item == null)
				continue;
			if (!item.getType().equals(ConfigManager.getWalletItem()))
				continue;
			if (!item.hasItemMeta())
				continue;
			if (!item.getItemMeta().hasDisplayName())
				continue;
			if (!item.getItemMeta().hasLore())
				continue;
			if (!item.getItemMeta().getDisplayName().equals(ChatColor.RESET + "" + ChatColor.YELLOW + "Wallet"))
				continue;
			if (!item.getItemMeta().getLore()
					.contains(ChatColor.translateAlternateColorCodes('&', "&a&oBalance: &l" + initialBalance + "$")))
				continue;

			wallet = item;
			break;
		}
		if (wallet == null)
			return;

		ItemMeta meta = wallet.getItemMeta();
		List<String> lore = meta.getLore();
		lore.clear();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a&oBalance: &l" + finalBalance + "$"));
		meta.setLore(lore);
		wallet.setItemMeta(meta);
	}

	public static List<ItemStack> getBalanceItems(double balance) {
		List<ItemStack> balanceItems = new ArrayList<ItemStack>();
		EconomyItems ecoItems = new EconomyItems();

		while (balance > 0) {
			if (balance >= 100) {
				balanceItems.add(ecoItems.oneHundredDollarBill());
				balance -= 100;

			} else if (balance >= 50) {
				balanceItems.add(ecoItems.fiftyDollarBill());
				balance -= 50;

			} else if (balance >= 20) {
				balanceItems.add(ecoItems.twentyDollarBill());
				balance -= 20;

			} else if (balance >= 10) {
				balanceItems.add(ecoItems.tenDollarBill());
				balance -= 10;

			} else if (balance >= 5) {
				balanceItems.add(ecoItems.fiveDollarBill());
				balance -= 5;

			} else if (balance >= 1) {
				balanceItems.add(ecoItems.oneDollarBill());
				balance -= 1;

			} else if (balance >= 0.25) {
				balanceItems.add(ecoItems.twentyFiveCentCoin());
				balance -= 0.5;

			} else if (balance >= 0.10) {
				balanceItems.add(ecoItems.tenCentCoin());
				balance -= 0.10;

			} else if (balance >= 0.05) {
				balanceItems.add(ecoItems.fiveCentCoin());
				balance -= 0.05;

			} else if (balance >= 0.01) {
				balanceItems.add(ecoItems.oneCentCoin());
				balance -= 0.01;

			} else
				balance = 0;
		}
		return balanceItems;
	}
}
