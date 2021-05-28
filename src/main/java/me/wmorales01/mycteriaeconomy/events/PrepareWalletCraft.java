package me.wmorales01.mycteriaeconomy.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import me.wmorales01.mycteriaeconomy.models.Wallet;
import me.wmorales01.mycteriaeconomy.util.Checker;
import me.wmorales01.mycteriaeconomy.util.Getter;

public class PrepareWalletCraft implements Listener {
	
	@EventHandler
	public void onWalletCraft(PrepareItemCraftEvent event) {
		if (event.getRecipe() == null)
			return;
		ItemStack result = event.getRecipe().getResult();
		if (result == null)
			return;
		Wallet wallet = new Wallet();
		if (!wallet.getItemStack().isSimilar(result))
			return;
		
		ItemStack[] matrix = event.getInventory().getMatrix();
		ItemStack bill = matrix[4];
		if (Checker.isBill(bill) && Getter.getValueFromBill(bill) == 1)
			return;
		
		event.getInventory().setResult(null);
	}

}
