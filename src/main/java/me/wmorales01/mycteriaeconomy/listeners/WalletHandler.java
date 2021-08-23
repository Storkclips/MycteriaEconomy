package me.wmorales01.mycteriaeconomy.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.inventories.WalletHolder;
import me.wmorales01.mycteriaeconomy.models.Wallet;
import me.wmorales01.mycteriaeconomy.util.BalanceManager;
import me.wmorales01.mycteriaeconomy.util.Checker;

public class WalletHandler implements Listener {
	private MycteriaEconomy plugin;

	public WalletHandler(MycteriaEconomy instance) {
		plugin = instance;
	}

	@EventHandler
	public void onWalletCraft(InventoryClickEvent event) {
		if (event.getInventory() == null || event.getClickedInventory() == null)
			return;

		if (event.getClickedInventory().getType() != InventoryType.WORKBENCH)
			return;

		CraftingInventory inventory = (CraftingInventory) event.getClickedInventory();
		ItemStack result = inventory.getResult();
		if (result == null)
			return;
		if (!result.hasItemMeta())
			return;
		if (!result.getItemMeta().hasLore())
			return;
		if (!result.getItemMeta().hasCustomModelData())
			return;
		if (!result.getItemMeta().getDisplayName().contains("Wallet"))
			return;

		Wallet wallet = new Wallet();
		inventory.setResult(wallet.getItemStack());
		plugin.addWallet(wallet);
	}

	@EventHandler
	public void onWalletRightClick(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (!action.equals(Action.RIGHT_CLICK_AIR) && !action.equals(Action.RIGHT_CLICK_BLOCK))
			return;

		Player player = event.getPlayer();
		ItemStack handItem = player.getInventory().getItem(event.getHand());

		if (!handItem.hasItemMeta())
			return;
		if (!handItem.getItemMeta().hasLore())
			return;
		if (!handItem.getItemMeta().hasDisplayName())
			return;
		if (!handItem.getItemMeta().getDisplayName().equals("Wallet"))
			return;

		Wallet wallet = Wallet.getByItemStack(handItem);
		if (wallet == null)
			return;

		Location playerLocation = player.getLocation();
		ItemMeta meta = handItem.getItemMeta();
		if (wallet.getBalance() == 0)
			meta.setCustomModelData(104); // Empty wallet
		else
			meta.setCustomModelData(105); // Filled wallet
		handItem.setItemMeta(meta);

		playerLocation.getWorld().playSound(playerLocation, Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 2);
		plugin.getOpenWallets().put(player.getUniqueId(), wallet);
		player.openInventory(wallet.getWalletGUI());
	}

	@EventHandler
	public void onGUIClick(InventoryClickEvent event) {
		if (event.getInventory() == null)
			return;
		if (event.getClickedInventory() == null)
			return;
		if (!(event.getInventory().getHolder() instanceof WalletHolder))
			return;

		Player player = (Player) event.getWhoClicked();
		Inventory clickedInventory = event.getClickedInventory();
		ClickType click = event.getClick();

		if (clickedInventory.getHolder() instanceof WalletHolder) {
			if (!click.isKeyboardClick()) {
				ItemStack placedItem = player.getItemOnCursor();
				if (placedItem == null || placedItem.getType() == Material.AIR)
					return;
				cancelIfIsntBill(placedItem, player, event);

			} else if (click.isKeyboardClick() && event.getHotbarButton() != -1) {
				ItemStack clickedItem = player.getInventory().getItem(event.getHotbarButton());
				cancelIfIsntBill(clickedItem, player, event);
			}
		} else {
			ItemStack clickedItem = event.getCurrentItem();
			if (clickedItem == null)
				return;
			if (Checker.isBill(clickedItem) || Checker.isCoin(clickedItem))
				return;

			event.setCancelled(true);
			player.updateInventory();
		}
	}

	@EventHandler
	public void onWalletClose(InventoryCloseEvent event) {
		Inventory inventory = event.getInventory();
		if (!(inventory.getHolder() instanceof WalletHolder))
			return;

		Player player = (Player) event.getPlayer();
		UUID uuid = player.getUniqueId();
		ItemStack[] walletContent = inventory.getContents();
		Wallet wallet = plugin.getOpenWallets().get(uuid);
		double balance = BalanceManager.getBalanceFromInventory(inventory);
		wallet.getContent().setContents(walletContent);
		wallet.setBalance(balance);
		for (int i = 0 ; i < player.getInventory().getContents().length; i++) {
			ItemStack item = player.getInventory().getContents()[i];
			if (item == null)
				continue;
			if (!wallet.isSimilar(item))
				continue;
			
			player.getInventory().setItem(i, wallet.getItemStack());
		}
		plugin.getOpenWallets().remove(uuid);
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			player.updateInventory();
		}, 1);
	}

	private void cancelIfIsntBill(ItemStack clickedItem, Player player, InventoryClickEvent event) {
		if (clickedItem == null)
			return;
		if (Checker.isBill(clickedItem))
			return;
		if (Checker.isCoin(clickedItem))
			return;

		event.setCancelled(true);
		player.updateInventory();
	}

}
