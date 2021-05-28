package me.wmorales01.mycteriaeconomy.models;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MachineItem {
	private ItemStack item;
	private Material material;
	private int stockAmount;
	private int sellAmount;
	private double price;
	private double sellPrice;

	public MachineItem(ItemStack item, int amount, double price) {
		this.item = item;
		this.material = item.getType();
		this.sellAmount = amount;
		this.price = price;
		this.setSellPrice(price);
	}

	public MachineItem(ItemStack item, int amount, double price, int stockAmount) {
		this.item = item;
		this.material = item.getType();
		this.sellAmount = amount;
		this.price = price;
		this.stockAmount = stockAmount;
		this.setSellPrice(price);
	}

	public ItemStack getSellItem(boolean useSellPrice) {
		ItemStack item = this.item.clone();
		ItemMeta meta = item.getItemMeta();
		List<String> lore = null;
		if (meta.hasLore())
			lore = meta.getLore();
		else
			lore = new ArrayList<String>();

		double price;
		if (useSellPrice)
			price = this.sellPrice;
		else
			price = this.price;
		lore.add(ChatColor.BLUE + "Amount: " + sellAmount);
		lore.add(ChatColor.GREEN + "Price: " + price);
		lore.add(ChatColor.DARK_AQUA + "Stock: " + stockAmount);
		meta.setLore(lore);
		item.setItemMeta(meta);

		return item;
	}

	public ItemStack getItemStack() {
		return item;
	}

	public Material getMaterial() {
		return material;
	}

	public int getSellAmount() {
		return sellAmount;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getStockAmount() {
		return stockAmount;
	}

	public void setStockAmount(int stockAmount) {
		this.stockAmount = stockAmount;
	}

	public void addStockAmount(int amount) {
		stockAmount += amount;
	}

	public void discountStockAmount(int amount) {
		stockAmount -= amount;
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}
}
