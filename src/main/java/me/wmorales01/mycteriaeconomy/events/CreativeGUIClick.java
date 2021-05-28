package me.wmorales01.mycteriaeconomy.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.wmorales01.mycteriaeconomy.inventories.CreativeHolder;

public class CreativeGUIClick implements Listener {
	
	@EventHandler
	public void onInventoryGUIClicl(InventoryClickEvent event) {
		if (event.getInventory() == null)
			return;
		if (event.getClickedInventory() == null)
			return;
		if (!(event.getClickedInventory().getHolder() instanceof CreativeHolder))
			return;
		
		ItemStack clickedItem = event.getCurrentItem();
		if (clickedItem == null)
			return;
		
		Player player = (Player) event.getWhoClicked();
		player.getInventory().addItem(clickedItem);
		
		event.setCancelled(true);
		player.updateInventory();
	}

}
