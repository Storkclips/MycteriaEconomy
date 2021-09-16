package me.wmorales01.mycteriaeconomy.inventories;

import me.wmorales01.mycteriaeconomy.models.AbstractShop;
import me.wmorales01.mycteriaeconomy.models.ShopItem;
import me.wmorales01.mycteriaeconomy.models.ShopItemProperty;
import me.wmorales01.mycteriaeconomy.util.GUIUtil;
import me.wmorales01.mycteriaeconomy.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class ShopItemEditorGUI {

    /**
     * Returns the Shop Item editor GUI where players can edit the price and sell amount of the Shop Items of a
     * Machine.
     *
     * @param shopItem The Shop Item that will be modified on the GUI.
     * @param shop     The Shop that contains the Shop Item.
     * @return The Shop Item Editor GUI.
     */
    public Inventory getGUI(ShopItem shopItem, AbstractShop shop) {
        Inventory inventory = Bukkit.createInventory(new ShopItemEditorHolder(shopItem, shop),
                27, "Editing Machine Item");
        GUIUtil.setFrame(inventory, Material.BLUE_STAINED_GLASS_PANE);
        addPropertyItems(inventory, shopItem);
        GUIUtil.fillEmpty(inventory, Material.CYAN_STAINED_GLASS_PANE);
        GUIUtil.addBackItem(inventory);
        return inventory;
    }

    /**
     * Adds the GUI items that will act as buttons to modify the different properties of the passed Shop Item.
     *
     * @param inventory Inventory where the GUI buttons will be added to.
     * @param shopItem  Shop Item which properties will be modified.
     */
    private void addPropertyItems(Inventory inventory, ShopItem shopItem) {
        inventory.setItem(12, GUIUtil.getGUIItem(ShopItemProperty.SELL_AMOUNT.getIcon(), "&eSell Amount: &3&l" +
                shopItem.getSellAmount(), null));
        inventory.setItem(14, GUIUtil.getGUIItem(ShopItemProperty.PRICE.getIcon(), "&ePrice: &3&l$" +
                StringUtil.roundNumber(shopItem.getPrice(), 2), null));
    }
}
