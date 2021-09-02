package me.wmorales01.mycteriaeconomy.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.ListIterator;

public class GUIUtil {

    public static void setFrame(Inventory inventory, Material frameMaterial) {
        ItemStack filler = getFiller(frameMaterial);
        int inventorySize = inventory.getSize();
        for (int i = 0; i < inventory.getSize(); i++) {
            if ((i > 9 && i < 17) || (i > 18 && i < 26 && inventorySize > 27)
                    || (i > 27 && i < 35 && inventorySize > 36)
                    || (i > 36 && i < 44 && inventorySize > 45))
                continue;

            inventory.setItem(i, filler);
        }
    }

    public static void fillEmpty(Inventory inventory, Material fillMaterial) {
        ItemStack filler = getFiller(fillMaterial);
        int emptySlot = inventory.firstEmpty();
        while (emptySlot != -1) {
            inventory.setItem(emptySlot, filler);
            emptySlot = inventory.firstEmpty();
        }
    }

    public static void setPageItems(Inventory inventory, int page, boolean hasNextPage) {
        if (page > 1) {
            inventory.setItem(inventory.getSize() - 8, getGUIItem(Material.PAPER, "&6Previous Page", null));
        }
        if (hasNextPage) {
            inventory.setItem(53, getGUIItem(Material.PAPER, "&6Next Page", null));
        }
    }

    public static void addBackItem(Inventory inventory) {
        inventory.setItem(0, getGUIItem(Material.RED_STAINED_GLASS_PANE, "&c&lGo Back", null));
    }

    public static ItemStack getGUIItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(StringUtil.formatColor(name));
        if (lore != null) {
            ListIterator<String> loreIterator = lore.listIterator();
            while (loreIterator.hasNext()) {
                loreIterator.set(StringUtil.formatColor(loreIterator.next()));
            }
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isFiller(ItemStack item) {
        if (item == null) return false;
        if (!item.hasItemMeta()) return false;
        if (!item.getItemMeta().hasDisplayName()) return false;

        return item.getItemMeta().getDisplayName().equals(" ");
    }

    public static int getFramedInventorySize(int itemAmount) {
        int inventorySize = 18 + 9 * (int) Math.ceil(itemAmount / 7.0);
        if (inventorySize > 54) return 54;

        return Math.max(27, inventorySize);
    }

    public static int getInitialIndex(int page) {
        return page == 1 ? 0 : (page - 1) * 28;
    }

    public static ItemStack getFiller(Material material) {
        ItemStack filler = new ItemStack(material);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);
        return filler;
    }
}
