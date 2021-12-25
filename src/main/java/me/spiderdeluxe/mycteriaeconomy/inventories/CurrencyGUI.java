package me.spiderdeluxe.mycteriaeconomy.inventories;

import me.spiderdeluxe.mycteriaeconomy.models.CurrencyItem;
import me.spiderdeluxe.mycteriaeconomy.util.GUIUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * This class contains the CurrencyGUI where the player can see and get all the available Currency Items from the
 * plugin.
 * <p>
 * It works in a very similar way to a creative GUI.
 */
public class CurrencyGUI {
    /**
     * @return Inventory with all the currency items on it
     */
    public Inventory getGUI() {
        final List<ItemStack> currencyItems = CurrencyItem.getEconomyItems();
        final int inventorySize = GUIUtil.getFramedInventorySize(currencyItems.size());
        final Inventory inventory = Bukkit.createInventory(new CurrencyHolder(), inventorySize, "Currency GUI");
        GUIUtil.setFrame(inventory, Material.YELLOW_STAINED_GLASS_PANE);
        addCurrencyItems(inventory, currencyItems);
        GUIUtil.fillEmpty(inventory, Material.LIME_STAINED_GLASS_PANE);
        return inventory;
    }

    /**
     * Adds the passed currency items to the passed inventory
     *
     * @param inventory     the inventory currency items will be added to
     * @param currencyItems the list of currency items that will be added to the inventory
     */
    private void addCurrencyItems(final Inventory inventory, final List<ItemStack> currencyItems) {
        for (final ItemStack currencyItem : currencyItems) {
            inventory.addItem(currencyItem);
        }
    }
}
