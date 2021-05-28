package me.wmorales01.mycteriaeconomy.inventories;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import me.wmorales01.mycteriaeconomy.models.EconomyItems;
import me.wmorales01.mycteriaeconomy.models.TradingMachine;
import me.wmorales01.mycteriaeconomy.models.VendingMachine;

public class CreativeGUI {

	public static Inventory getCreativeGUI() {
		Inventory creativeInventory = Bukkit.createInventory(new CreativeHolder(), 54, "Creative GUI");
		EconomyItems items = new EconomyItems();
		creativeInventory.addItem(items.oneDollarBill());
		creativeInventory.addItem(items.fiveDollarBill());
		creativeInventory.addItem(items.tenDollarBill());
		creativeInventory.addItem(items.twentyDollarBill());
		creativeInventory.addItem(items.fiftyDollarBill());
		creativeInventory.addItem(items.oneHundredDollarBill());
		creativeInventory.addItem(items.oneCentCoin());
		creativeInventory.addItem(items.fiveCentCoin());
		creativeInventory.addItem(items.tenCentCoin());
		creativeInventory.addItem(items.twentyFiveCentCoin());
		creativeInventory.addItem(VendingMachine.getItemStack());
		creativeInventory.addItem(TradingMachine.getItemStack());
		return creativeInventory;
	}
}
