package me.wmorales01.mycteriaeconomy.listeners;

import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.MachineItem;
import me.wmorales01.mycteriaeconomy.models.MachineOperator;
import me.wmorales01.mycteriaeconomy.models.NPCOperator;
import me.wmorales01.mycteriaeconomy.util.Getter;
import me.wmorales01.mycteriaeconomy.util.Messager;

public class PlayerChat implements Listener {
	private MycteriaEconomy plugin;

	public PlayerChat(MycteriaEconomy instance) {
		plugin = instance;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		MachineOperator operator = MachineOperator.getOperator(player);
		if (operator == null)
			return;

		event.setCancelled(true);
		ItemStack selectedItem = operator.getSelectedItem();
		String message = event.getMessage().trim();
		if (message.equalsIgnoreCase("cancel")) {
			plugin.getVendingOperators().remove(operator);
			event.setCancelled(true);
			return;
		}

		String[] parameters = message.split("-");
		if (parameters.length < 2 || parameters.length > 2) {
			Messager.sendMessage(player, "&cUsage: &lAmount - Price");
			event.setCancelled(true);
			return;
		}
		Integer amount = Getter.getIntFromString(player, parameters[0].trim());
		Double price = Getter.getDoubleFromString(player, parameters[1].trim());
		if (amount == null || price == null)
			return;
		if (amount <= 0 || price <= 0) {
			Messager.sendMessage(player, "&cYou must enter a value higher than 0 for both amount and price.");
			return;

		} else if (amount > selectedItem.getMaxStackSize()) {
			Messager.sendMessage(player, "&cThis amount exceeds the max amount for this item.");
			return;

		} else if (price > Double.MAX_VALUE) {
			Messager.sendMessage(player, "&cThis sell price is too high!");
			return;
		}
		ItemMeta meta = selectedItem.getItemMeta();
		if (meta.hasLore()) {
			List<String> lore = meta.getLore();
			ListIterator<String> iterator = lore.listIterator();
			while (iterator.hasNext()) {
				String line = iterator.next();
				if (!line.startsWith(ChatColor.BLUE + "Amount:"))
					continue;

				iterator.remove();
			}
			meta.setLore(lore);
			selectedItem.setItemMeta(meta);
		}
		MachineItem machineItem = new MachineItem(selectedItem.clone(), amount, price);
		if (operator instanceof MachineOperator && operator.getMachine() != null) {
			operator.getMachine().addStock(machineItem);
			plugin.getVendingOperators().remove(operator);
			Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(operator.getMachine().getOwnerGUI(player)));
		} else {
			((NPCOperator) operator).getShop().addStock(machineItem);
			plugin.getNpcOperators().remove(operator);
			((NPCOperator) operator).getShop().getOwnerGUI(player);
		}
		Messager.sendMessage(player, "&aItem successfully added to machine stock.");
	}
}
