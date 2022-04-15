package me.spiderdeluxe.mycteriaeconomy.models.shop;

import lombok.Getter;
import me.spiderdeluxe.mycteriaeconomy.models.Wallet;
import me.spiderdeluxe.mycteriaeconomy.menu.shop.ShopMenu;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShopListener implements Listener {

	/**
	 * Stores active operator by player
	 */
	@Getter
	private static final Set<Player> shopEditors = new HashSet<>();

	/**
	 * Stores active traders by player
	 */
	@Getter
	private static Set<Player> itemsCustomizers = new HashSet<>();

	/**
	 * Stores active linkers by player
	 */
	@Getter
	private static Map<Shop, OfflinePlayer> shopLinkers = new HashMap<>();


	@EventHandler
	public void onInventoryInteract(final InventoryClickEvent event) {

		if (event.getClickedInventory() == null) return;

		final Player player = (Player) event.getWhoClicked();
		final ItemStack item = event.getCurrentItem();

		if (item == null || item.getType() == Material.AIR || Wallet.isWallet(item)) return;

		if (event.getClickedInventory() != player.getInventory()) return;

		if (ShopListener.isEditor(player)) {

			final ShopMenu shopMenu = (ShopMenu) ShopMenu.getMenu(player);
			shopMenu.addNewItem(item);
		}
	}


	public static boolean isEditor(final Player player) {
		return shopEditors.contains(player);
	}

	public static void addEditor(final Player player) {
		if (!isEditor(player))
			shopEditors.add(player);
	}

	public static void removeEditor(final Player player) {
		if (isEditor(player))
			shopEditors.remove(player);
	}


	public static boolean isCustomizer(final Player player) {
		return itemsCustomizers.contains(player);
	}

	public static void addCustomizer(final Player player) {
		if (!isCustomizer(player))
			itemsCustomizers.add(player);
	}

	public static void removeCustomizer(final Player player) {
		if (isCustomizer(player))
			itemsCustomizers.remove(player);
	}


	public static boolean isLinkers(final OfflinePlayer player) {
		return shopLinkers.containsValue(player);
	}

	public static boolean isLinkers(final Shop shop, final OfflinePlayer player) {
		if (shopLinkers.containsKey(shop))
			return shopLinkers.get(shop).equals(player);

		return false;
	}

	public static void addLinkers(final Shop shop, final OfflinePlayer player) {
		if (!isLinkers(shop, player))
			shopLinkers.put(shop, player);
	}

	public static void removeLinkers(final Shop shop, final OfflinePlayer player) {
		shopLinkers.remove(player);

		if (isLinkers(shop, player))
			shopLinkers.remove(shop, player);
	}

	public static Shop findShopByPlayerLinkers(final OfflinePlayer player ) {
		if (shopLinkers.containsValue(player))
			for (final Shop shop  : shopLinkers.keySet()) {
				if(shop.getOwner() == ShopOwnerType.PLAYER)
				if(shop.getOwnerPlayer().getPlayer() == player.getPlayer())
					return shop;
			}
		return null;
	}

}
