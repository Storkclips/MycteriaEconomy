package me.wmorales01.mycteriaeconomy.listeners;

import me.wmorales01.mycteriaeconomy.inventories.CurrencyHolder;
import me.wmorales01.mycteriaeconomy.util.InventoryUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CurrencyGUIHandler implements Listener {

    @EventHandler
    public void onInventoryGUIClicl(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!(event.getClickedInventory().getHolder() instanceof CurrencyHolder)) return;

        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;
        if (event.getClick().isShiftClick()) {
            clickedItem.setAmount(64);
        }
        Player player = (Player) event.getWhoClicked();
        InventoryUtil.giveItem(player, clickedItem);
    }
}
