package me.wmorales01.mycteriaeconomy.util;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;

public class InventoryUtil {

    public static void giveItem(Player player, ItemStack item) {
        if (item == null || item.getType().isAir()) return;

        PlayerInventory inventory = player.getInventory();
        if (inventory.firstEmpty() != -1) {
            inventory.addItem(item);
            return;
        }
        player.getWorld().dropItemNaturally(player.getLocation(), item);
    }

    public static void giveItems(Player player, Collection<ItemStack> items) {
        for (ItemStack item : items) {
            giveItem(player, item);
        }
    }

    // Puts the passed item into the the player's inventory 100% of the times, even if it means dropping an item
    // from their current inventory
    public static void assureGiveItem(Player player, ItemStack item) {
        PlayerInventory inventory = player.getInventory();
        if (inventory.firstEmpty() != -1) {
            inventory.addItem(item);
            return;
        }
        ItemStack toDrop = inventory.getItem(9);
        player.getWorld().dropItem(player.getLocation(), toDrop.clone());
        toDrop.setAmount(0);
        inventory.addItem(item);
    }

    // Returns if the passed checkItem is similar to the target item without taking into account the item quantity
    public static boolean isSimilar(ItemStack checkItem, ItemStack targetItem) {
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
    public static int countItem(ItemStack target, Inventory inventory) {
        int totalAmount = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item == null) continue;
            if (!isSimilar(target, item)) continue;

            totalAmount += item.getAmount();
        }
        return totalAmount;
    }

    /**
     * Opens the passed inventory on the main thread to the passed player.
     *
     * @param player    Player which will be opened with the passed Inventory.
     * @param inventory Inventory that will be opened to the passed Player.
     */
    public static void openInventoryAsync(Player player, Inventory inventory) {
        Bukkit.getScheduler().runTask(MycteriaEconomy.getInstance(), () -> player.openInventory(inventory));
    }

    /**
     * Goes through all the slots of the passed inventory counting how many of the passed ItemStack can fit in the
     * inventory.
     *
     * @param target    ItemStack that will be counted for available slots.
     * @param inventory Inventory whose slots will be counted.
     * @return The amount of the passed ItemStack that can fit on the passed Inventory.
     */
    public static int countAvailableSlots(ItemStack target, Inventory inventory) {
        int targetAmount = target.getAmount();
        int availableSlots = 0;
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack slotItem = inventory.getItem(slot);
            if (slotItem == null || slotItem.getType().isAir()) {
                availableSlots += 64 / targetAmount;
                continue;
            }
            if (!isSimilar(slotItem, target)) continue;

            int remainingSlotAmount = 64 - slotItem.getAmount();
            availableSlots += remainingSlotAmount / targetAmount;
        }
        return availableSlots;
    }
}
