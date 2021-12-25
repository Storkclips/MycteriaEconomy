package me.spiderdeluxe.mycteriaeconomy.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.ListIterator;

public class GUIUtil {

    public static void setFrame(final Inventory inventory, final Material frameMaterial) {
        final ItemStack filler = getFiller(frameMaterial);
        final int inventorySize = inventory.getSize();
        for (int i = 0; i < inventory.getSize(); i++) {
            if ((i > 9 && i < 17) || (i > 18 && i < 26 && inventorySize > 27)
                    || (i > 27 && i < 35 && inventorySize > 36)
                    || (i > 36 && i < 44 && inventorySize > 45))
                continue;

            inventory.setItem(i, filler);
        }
    }

    public static void fillEmpty(final Inventory inventory, final Material fillMaterial) {
        final ItemStack filler = getFiller(fillMaterial);
        int emptySlot = inventory.firstEmpty();
        while (emptySlot != -1) {
            inventory.setItem(emptySlot, filler);
            emptySlot = inventory.firstEmpty();
        }
    }

    public static ItemStack getGUIItem(final Material material, final String name, final List<String> lore) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(StringUtil.formatColor(name));
        if (lore != null) {
            final ListIterator<String> loreIterator = lore.listIterator();
            while (loreIterator.hasNext()) {
                loreIterator.set(StringUtil.formatColor(loreIterator.next()));
            }
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static int getFramedInventorySize(final int itemAmount) {
        final int inventorySize = 18 + 9 * (int) Math.ceil(itemAmount / 7.0);
        if (inventorySize > 54) return 54;

        return Math.max(27, inventorySize);
    }

    public static ItemStack getFiller(final Material material) {
        final ItemStack filler = new ItemStack(material);
        final ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);
        return filler;
    }
}
