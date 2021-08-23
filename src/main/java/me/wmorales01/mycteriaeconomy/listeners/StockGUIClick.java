package me.wmorales01.mycteriaeconomy.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.wmorales01.mycteriaeconomy.inventories.StockHolder;
import me.wmorales01.mycteriaeconomy.util.Checker;

public class StockGUIClick implements Listener {

	@EventHandler
	public void onStockGUIClick(InventoryClickEvent event) {
		if (event.getInventory() == null || event.getClickedInventory() == null)
			return;
		if (!(event.getInventory().getHolder() instanceof StockHolder))
			return;

		StockHolder holder = (StockHolder) event.getInventory().getHolder();
		Player player = (Player) event.getWhoClicked();
		ClickType click = event.getClick();
		Material stockMaterial = holder.getMachineItem().getItemStack().getType();
		Inventory clickedInventory = event.getClickedInventory();

		if (clickedInventory.getType() == InventoryType.PLAYER) {
			ItemStack clickedItem = event.getCurrentItem();
			if (clickedItem == null || Checker.isBill(clickedItem) || Checker.isCoin(clickedItem)
					|| clickedItem.getType() != stockMaterial) {
				event.setCancelled(true);
				player.updateInventory();
				return;
			}

		} else if (click.isKeyboardClick()) {
			ItemStack selectedItem = player.getInventory().getItem(event.getHotbarButton());
			if (selectedItem == null || Checker.isBill(selectedItem) || Checker.isCoin(selectedItem)
					|| selectedItem.getType() != stockMaterial) {
				event.setCancelled(true);
				player.updateInventory();
				return;
			}
		}
	}
}
