package me.spiderdeluxe.mycteriaeconomy.event;

import me.spiderdeluxe.mycteriaeconomy.inventories.CurrencyHolder;
import me.spiderdeluxe.mycteriaeconomy.util.InventoryUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CurrencyGUIListener implements Listener {

    @EventHandler
    public void onInventoryGUIClick(final InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!(event.getClickedInventory().getHolder() instanceof CurrencyHolder)) return;

        event.setCancelled(true);
        final ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;
        if (event.getClick().isShiftClick()) {
            clickedItem.setAmount(64);
        }
        final Player player = (Player) event.getWhoClicked();
        InventoryUtil.giveItem(player, clickedItem);
    }
}
