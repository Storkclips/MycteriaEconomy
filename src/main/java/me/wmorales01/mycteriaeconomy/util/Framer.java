package me.wmorales01.mycteriaeconomy.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Framer {

	public static void setInventoryFrame(Inventory inventory, Material material) {
		ItemStack filler = new ItemStack(material);
		ItemMeta meta = filler.getItemMeta();
		
		meta.setDisplayName(" ");
		filler.setItemMeta(meta);
		
		for (int i = 0 ; i < inventory.getSize(); i++) {
			if (i > 9 && i < 17)
				continue;
			else if (i > 18 && i < 26)
				continue;
			else if (i > 27 && i < 35)
				continue;
			else if (i > 36 && i < 44)
				continue;
			
			inventory.setItem(i, filler);
		}
	}
}
