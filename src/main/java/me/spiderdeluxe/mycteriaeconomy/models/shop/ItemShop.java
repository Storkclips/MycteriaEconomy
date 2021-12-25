package me.spiderdeluxe.mycteriaeconomy.models.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.spiderdeluxe.mycteriaeconomy.util.StringUtil;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

// This file holds information about the category's item
@Getter
@AllArgsConstructor
public class ItemShop implements ConfigSerializable {

	//The uuid of this item
	UUID uuid;

	//The name of item
	final String name;


	//The selling options
	@Setter
	int quantity;
	@Setter
	double price;
	@Setter
	int stock;

	@Getter
	@Setter
	private ItemStack itemStack;

	// This method does the magic when you use save() method above
	// and converts this class into a hash map you can save in your yml files
	@Override
	public SerializedMap serialize() {
		final SerializedMap map = new SerializedMap();

		map.put("UUID", uuid);
		map.put("Name", name);

		map.put("Quantity", quantity);
		map.put("Price", price);
		map.put("Stock", stock);

		map.put("Item_Info", itemStack);

		return map;
	}


	// --------------------------------------------------------------------------------------------------------------
	// ItemStacks Util
	// --------------------------------------------------------------------------------------------------------------


	/**
	 * Creates an ItemStack with the Item Key and a lore detailing price, stock and sell amount to use on the
	 * shop GUI.
	 * <p>
	 * Depending on if it is a Vending Item or not the item will have lore indicating to click differently to buy or sell
	 * different amounts.
	 *
	 * @param isVendingItem true if the item will be exhibited in a Vending Shop, false if it will be exhibited on a
	 *                      Trading Shop.
	 * @return ItemStack with all the Machine Item's info.
	 */
	public ItemStack getExhibitionItem(final boolean isVendingItem) {
		final String operation = isVendingItem ? "buy" : "sell";
		final List<String> lores = new ArrayList<>(getInfoLore());
		lores.addAll(Arrays.asList("&2- &a&lLeft Click &ato " + operation + " 1.",
				"&2- &a&lRight Click &ato " + operation + " a custom quantity."));

		final ItemStack item = ItemCreator.of(CompMaterial.DIRT)
				.name(getItemStack().getItemMeta().getDisplayName())
				.lores(lores)
				.build().make();

		item.setType(getItemStack().getType());
		return item;
	}

	/**
	 * Creates and returns an ItemStack that contains the Machine Item key and a lore detailing price, stock and sell
	 * amount.
	 * <p>
	 * Additionally, the item has a lore with instructions to configure the Shop Item in the Owner GUI.
	 *
	 * @return ItemStack with details of the ShopItem and instructions to configure it on the Owner GUI.
	 */
	public ItemStack getConfigurationItem() {
		final List<String> lores = new ArrayList<>(getInfoLore());
		lores.addAll(Arrays.asList("&2- &a&lLeft Click &ato modify this item's properties.",
				"&2- &a&lRight Click &ato delete this item from the Machine."));

		final ItemStack item = ItemCreator.of(CompMaterial.DIRT)
				.name(getItemStack().getItemMeta().getDisplayName())
				.lores(lores)
				.build().make();

		item.setType(getItemStack().getType());
		return item;
	}

	/**
	 * Creates and returns an List of string with the price, sell amount and remaining stock of the Item.
	 *
	 * @return List of string that indicates the basic information regarding the Item.
	 */
	private List<String> getInfoLore() {
		return Arrays.asList("&ePrice: &3&l$" + StringUtil.roundNumber(price, 2),
				"&eSell Amount: &3&l" + quantity,
				"&eStock: &3&l" + stock);
	}
}