package me.spiderdeluxe.mycteriaeconomy.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;

public class InventoryUtil {

    public static void giveItem(final Player player, final ItemStack item) {
        if (item == null || item.getType().isAir()) return;

        final PlayerInventory inventory = player.getInventory();
        if (inventory.firstEmpty() != -1) {
            inventory.addItem(item);
            return;
        }
        player.getWorld().dropItemNaturally(player.getLocation(), item);
    }

    public static void giveItems(final Player player, final Collection<ItemStack> items) {
        for (final ItemStack item : items) {
            giveItem(player, item);
        }
    }

    // Returns if the passed checkItem is similar to the target item without taking into account the item quantity
    public static boolean isSimilar(ItemStack checkItem, final ItemStack targetItem) {
        if (checkItem == null) return false;

        checkItem = checkItem.clone();
        checkItem.setAmount(targetItem.getAmount());
        return checkItem.isSimilar(targetItem);
    }


    /**
     * Iterates through all the items in the passed inventory searching for the passed ItemStack and returns the total
     * amount of it.
     *
     * @param target    ItemStack that will be searched for.
     * @param inventory Inventory where the ItemStack will be searched.
     * @return The amount of item coincidences of the passed ItemStack.
     */
    public static int countItem(final ItemStack target, final Inventory inventory) {
        int totalAmount = 0;
        for (final ItemStack item : inventory.getContents()) {
            if (item == null) continue;

            if (!isSimilar(target, item)) continue;

            totalAmount += (item.getAmount());
        }
        return totalAmount;
    }

    /**
     * Goes through all the slots of the passed inventory counting how many of the passed ItemStack can fit in the
     * inventory.
     *
     * @param target    ItemStack that will be counted for available slots.
     * @param inventory Inventory whose slots will be counted.
     * @return The amount of the passed ItemStack that can fit on the passed Inventory.
     */
    public static int countAvailableSlots(final ItemStack target, final Inventory inventory) {
        final int targetAmount = target.getAmount();
        int availableSlots = 0;
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            final ItemStack slotItem = inventory.getItem(slot);
            if (slotItem == null || slotItem.getType().isAir()) {
                availableSlots += 64 / targetAmount;
                continue;
            }
            if (!isSimilar(slotItem, target)) continue;

            final int remainingSlotAmount = 64 - slotItem.getAmount();
            availableSlots += remainingSlotAmount / targetAmount;
        }
        return availableSlots;
    }
}
