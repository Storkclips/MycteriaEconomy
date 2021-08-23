package me.wmorales01.mycteriaeconomy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import me.wmorales01.mycteriaeconomy.inventories.StockHolder;
import me.wmorales01.mycteriaeconomy.models.MachineItem;

public class StockGUIClose implements Listener {

	@EventHandler
	public void onStockGUIClose(InventoryCloseEvent event) {
		if (event.getInventory() == null)
			return;
		if (!(event.getInventory().getHolder() instanceof StockHolder))
			return;
		
		StockHolder holder = (StockHolder) event.getInventory().getHolder();
		MachineItem machineItem = holder.getMachineItem();
		
		int amount = 0;
		for (ItemStack item : event.getInventory().getContents()) {
			if (item == null)
				continue;
			if (item.getType() != machineItem.getMaterial())
				continue;
			
			amount += item.getAmount();
		}
		
		machineItem.setStockAmount(amount);
	}
}
