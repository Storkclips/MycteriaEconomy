package me.wmorales01.mycteriaeconomy.models;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EconomyItems {

	public ItemStack oneDollarBill() {
		ItemStack bill = new ItemStack(Material.PAPER);
		ItemMeta meta = bill.getItemMeta();
		
		meta.setDisplayName(ChatColor.RESET + "One Dollar Bill");
		meta.setCustomModelData(101);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bill.setItemMeta(meta);
		
		return bill;
	}
	
	public ItemStack fiveDollarBill() {
		ItemStack bill = new ItemStack(Material.PAPER);
		ItemMeta meta = bill.getItemMeta();
		
		meta.setDisplayName(ChatColor.RESET + "Five Dollar Bill");
		meta.setCustomModelData(102);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bill.setItemMeta(meta);
		
		return bill;
	}
	
	public ItemStack tenDollarBill() {
		ItemStack bill = new ItemStack(Material.PAPER);
		ItemMeta meta = bill.getItemMeta();
		
		meta.setDisplayName(ChatColor.RESET + "Ten Dollar Bill");
		meta.setCustomModelData(103);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bill.setItemMeta(meta);
		
		return bill;
	}
	
	public ItemStack twentyDollarBill() {
		ItemStack bill = new ItemStack(Material.PAPER);
		ItemMeta meta = bill.getItemMeta();
		
		meta.setDisplayName(ChatColor.RESET + "Twenty Dollar Bill");
		meta.setCustomModelData(104);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bill.setItemMeta(meta);
		
		return bill;
	}
	
	public ItemStack fiftyDollarBill() {
		ItemStack bill = new ItemStack(Material.PAPER);
		ItemMeta meta = bill.getItemMeta();
		
		meta.setDisplayName(ChatColor.RESET + "Fifty Dollar Bill");
		meta.setCustomModelData(105);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bill.setItemMeta(meta);
		
		return bill;
	}
	
	public ItemStack oneHundredDollarBill() {
		ItemStack bill = new ItemStack(Material.PAPER);
		ItemMeta meta = bill.getItemMeta();
		
		meta.setDisplayName(ChatColor.RESET + "One Hundred Dollar Bill");
		meta.setCustomModelData(106);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bill.setItemMeta(meta);
		
		return bill;
	}
	
	public ItemStack oneCentCoin() {
		ItemStack coin = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = coin.getItemMeta();
		
		meta.setDisplayName(ChatColor.RESET + "One Cent Coin");
		meta.setCustomModelData(101);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		coin.setItemMeta(meta);
		
		return coin;
	}
	
	public ItemStack fiveCentCoin() {
		ItemStack coin = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = coin.getItemMeta();
		
		meta.setDisplayName(ChatColor.RESET + "Five Cent Coin");
		meta.setCustomModelData(102);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		coin.setItemMeta(meta);
		
		return coin;
	}
	
	public ItemStack tenCentCoin() {
		ItemStack bill = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = bill.getItemMeta();
		
		meta.setDisplayName(ChatColor.RESET + "Ten Cent Coin");
		meta.setCustomModelData(103);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bill.setItemMeta(meta);
		
		return bill;
	}
	
	public ItemStack twentyFiveCentCoin() {
		ItemStack bill = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = bill.getItemMeta();
		
		meta.setDisplayName(ChatColor.RESET + "Twenty Five Cent Coin");
		meta.setCustomModelData(104);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bill.setItemMeta(meta);
		
		return bill;
	}
	
	
}
