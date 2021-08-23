package me.wmorales01.mycteriaeconomy.listeners;

import java.text.DecimalFormat;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.wmorales01.mycteriaeconomy.inventories.ATMHolder;
import me.wmorales01.mycteriaeconomy.models.EconomyItems;
import me.wmorales01.mycteriaeconomy.models.EconomyPlayer;
import me.wmorales01.mycteriaeconomy.util.Checker;
import me.wmorales01.mycteriaeconomy.util.Getter;
import me.wmorales01.mycteriaeconomy.util.Messager;

public class ATMGUIClick implements Listener {

	@EventHandler
	public void onATMGUIClick(InventoryClickEvent event) {
		if (event.getInventory() == null)
			return;
		if (!(event.getInventory().getHolder() instanceof ATMHolder))
			return;
		if (event.getClickedInventory() == null)
			return;

		Player player = (Player) event.getWhoClicked();
		event.setCancelled(true);
		player.updateInventory();

		ItemStack clickedItem = event.getCurrentItem();
		EconomyPlayer ecoPlayer = EconomyPlayer.fromPlayer(player);
		Inventory inventory = event.getClickedInventory();
		if (clickedItem == null)
			return;

		if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
			if (clickedItem.getItemMeta().hasCustomModelData()) {
				int modelData = clickedItem.getItemMeta().getCustomModelData();
				EconomyItems items = new EconomyItems();
				
				if (clickedItem.getType() == Material.PAPER) {
					switch (modelData) {
					case 101:
						executeWithdraw(ecoPlayer, inventory, 1, items.oneDollarBill());
						break;

					case 102:
						executeWithdraw(ecoPlayer, inventory, 5, items.fiveDollarBill());
						break;

					case 103:
						executeWithdraw(ecoPlayer, inventory, 10, items.tenDollarBill());
						break;

					case 104:
						executeWithdraw(ecoPlayer, inventory, 20, items.twentyDollarBill());
						break;

					case 105:
						executeWithdraw(ecoPlayer, inventory, 50, items.fiftyDollarBill());
						break;

					case 106:
						executeWithdraw(ecoPlayer, inventory, 100, items.oneHundredDollarBill());
						break;

					default:
						break;
					}
				} else if (clickedItem.getType() == Material.IRON_NUGGET) {
					switch (modelData) {
					case 101:
						executeWithdraw(ecoPlayer, inventory, 0.01, items.oneCentCoin());
						break;

					case 102:
						executeWithdraw(ecoPlayer, inventory, 0.05, items.fiveCentCoin());
						break;

					case 103:
						executeWithdraw(ecoPlayer, inventory, 0.10, items.tenCentCoin());
						break;

					case 104:
						executeWithdraw(ecoPlayer, inventory, 0.25, items.twentyFiveCentCoin());
						break;
					}
				}
			}

		} else {
			if (!Checker.isBill(clickedItem) && !Checker.isCoin(clickedItem))
				return;
			
			executeDeposit(ecoPlayer, clickedItem, event.getInventory().getItem(22));
			player.getInventory().setItem(event.getSlot(), null);
		}
	}

	private void executeWithdraw(EconomyPlayer ecoPlayer, Inventory inventory, double transactionValue,
			ItemStack item) {
		Player player = ecoPlayer.getPlayer();

		if (ecoPlayer.getBankBalance() - transactionValue <= 0) {
			Messager.sendMessage(player, "&cYou don't have enough bank balance to execute this transaction.");
			return;
		}
		ecoPlayer.removeBankBalance(transactionValue);
		Location playerLocation = player.getLocation();

		if (player.getInventory().firstEmpty() == -1)
			playerLocation.getWorld().dropItemNaturally(playerLocation, item);
		else
			player.getInventory().addItem(item);

		playerLocation.getWorld().playSound(playerLocation, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);

		ItemStack balanceItem = inventory.getItem(22);
		updateBalanceItem(ecoPlayer, balanceItem);
	}

	private void executeDeposit(EconomyPlayer ecoPlayer, ItemStack economyItem, ItemStack balanceItem) {
		double value = 0;
		if (Checker.isBill(economyItem))
			value = Getter.getValueFromBill(economyItem);
		else if (Checker.isCoin(economyItem))
			value = Getter.getValueFromCoin(economyItem);

		if (value == 0)
			return;

		ecoPlayer.addBankBalance(value);
		Player player = ecoPlayer.getPlayer();
		Location playerLocation = player.getLocation();
		updateBalanceItem(ecoPlayer, balanceItem);
		
		playerLocation.getWorld().playSound(playerLocation, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
		
	}

	private void updateBalanceItem(EconomyPlayer ecoPlayer, ItemStack balanceItem) {
		ItemMeta meta = balanceItem.getItemMeta();
		List<String> lore = meta.getLore();
		lore.clear();
		DecimalFormat format = new DecimalFormat("###.##");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&6&l" + format.format(ecoPlayer.getBankBalance()) + "$"));
		meta.setLore(lore);
		balanceItem.setItemMeta(meta);
	}
}
