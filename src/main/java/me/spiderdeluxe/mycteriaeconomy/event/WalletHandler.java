package me.spiderdeluxe.mycteriaeconomy.event;

import me.spiderdeluxe.mycteriaeconomy.inventories.WalletHolder;
import me.spiderdeluxe.mycteriaeconomy.models.Wallet;
import me.spiderdeluxe.mycteriaeconomy.util.CurrencyItem;
import me.spiderdeluxe.mycteriaeconomy.util.SFXManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;

public class WalletHandler implements Listener {

    // Listens when a player prepares the craft of a recipe, if the recipe equals the Wallet craft recipe then
    // don't do anything. If the bill from the recipe is different from a one dollar bill then cancel the craft
    @EventHandler
    public void onWalletCraft(final PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;
        final ItemStack result = event.getRecipe().getResult();
        if (!Wallet.isWallet(result)) return;

        final ItemStack[] craftMatrix = event.getInventory().getMatrix();
        final ItemStack bill = craftMatrix[4];
        if (CurrencyItem.isCurrencyItem(bill) && CurrencyItem.getValueFromItem(bill) == 1) return;

        event.getInventory().setResult(null);
    }

    // Listens when a player right clicks and calls the attemptWalletOpen event
    @EventHandler
    public void onWalletRightClick(final PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_CLICK")) return;

        attemptWalletOpen(event.getPlayer(), event.getItem(), event);
    }

    // Idem, with the difference that it listens when a player right clicks an entity
    @EventHandler
    public void onPlayerRightClickEntity(final PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        final ItemStack usedItem = player.getInventory().getItem(event.getHand());
        attemptWalletOpen(player, usedItem, event);
    }

    // If the passed used item is a Wallet then open its corresponding GUI
    private void attemptWalletOpen(final Player player, final ItemStack usedItem, final Cancellable event) {
        final Wallet wallet = Wallet.fromItemStack(usedItem);
        if (wallet == null) return; // usedItem isn't wallet

        event.setCancelled(true);
        player.openInventory(wallet.getGUI());
        SFXManager.playPlayerSound(player, Sound.ITEM_ARMOR_EQUIP_LEATHER, 0.6F, 1.4F);
    }

    // Listens when a player clicks a GUI, if it is a Wallet GUI and the player is saving an item other than an
    // economy item then cancel the event
    @EventHandler
    public void onWalletGUIClick(final InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        if (!(event.getInventory().getHolder() instanceof WalletHolder)) return;

        final Player player = (Player) event.getWhoClicked();
        final ClickType clickType = event.getClick();
        final ItemStack savedItem;
        if (clickedInventory.getType() == InventoryType.CHEST) { // Clicked wallet GUI
//            if (!clickType.isShiftClick() && !clickType.isKeyboardClick()) { // Clicking to take an item from inventory
//                if (player.getItemOnCursor().getType().isAir()) return;
//            }
            if (clickType.isKeyboardClick() && event.getHotbarButton() != -1) {
                savedItem = player.getInventory().getItem(event.getHotbarButton());
            } else {
                savedItem = player.getItemOnCursor();
            }
        } else {
            if (!clickType.isShiftClick()) return;

            savedItem = event.getCurrentItem();
        }
        if (savedItem == null || savedItem.getType().isAir()) return;
        if (CurrencyItem.isCurrencyItem(savedItem)) return;

        event.setCancelled(true);
        player.updateInventory();
    }

    // Listens when a player drags an item into an inventory, if the inventory is a Wallet GUI and one of the
    // dragged items isn't an Economy Item then cancel the event
    @EventHandler
    public void onWalletGUIDrag(final InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof WalletHolder)) return;
        boolean draggedIntoWallet = false;
        for (final int slot : event.getRawSlots()) {
            if (slot > 35) continue;

            draggedIntoWallet = true;
        }
        if (!draggedIntoWallet) return;
        final Collection<ItemStack> draggedItems = event.getNewItems().values();
        for (final ItemStack item : draggedItems) {
            if (CurrencyItem.isCurrencyItem(item)) continue;

            event.setCancelled(true);
            return;
        }
    }

    // Listens when a player closes an Inventory, if it is a Wallet inventory the update the wallet's ItemStack
    @EventHandler
    public void onWalletClose(final InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof WalletHolder)) return;

        final Player player = (Player) event.getPlayer();
        final Wallet openWallet = ((WalletHolder) inventory.getHolder()).getWallet();
        openWallet.computeWalletBalance();
        // Update wallet ItemStack from the player inventory
        final PlayerInventory playerInventory = player.getInventory();
        for (int inventorySlot = 0; inventorySlot < playerInventory.getSize(); inventorySlot++) {
            final ItemStack item = playerInventory.getItem(inventorySlot);
            if (!openWallet.isSimilar(item)) continue;

            playerInventory.setItem(inventorySlot, openWallet.getItemStack());
        }
        player.updateInventory();
        openWallet.saveWalletData();
        SFXManager.playPlayerSound(player, Sound.ITEM_ARMOR_EQUIP_LEATHER, 0.6F, 0.8F);
    }
}
