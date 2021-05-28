package me.wmorales01.mycteriaeconomy.models;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;

public class MachineOperator {
	private Player player;
	private Machine machine;
	private ItemStack selectedItem;

	public MachineOperator(Player player, Machine machine, ItemStack selectedItem) {
		this.player = player;
		this.machine = machine;
		this.selectedItem = selectedItem;
	}

	public MachineOperator(Player player, ItemStack selectedItem) {
		this.player = player;
		this.selectedItem = selectedItem;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Machine getMachine() {
		return machine;
	}

	public void setMachine(VendingMachine machine) {
		this.machine = machine;
	}

	public ItemStack getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(ItemStack selectedItem) {
		this.selectedItem = selectedItem;
	}

	public static MachineOperator getOperator(Player player) {
		MycteriaEconomy plugin = MycteriaEconomy.getPlugin(MycteriaEconomy.class);
		for (MachineOperator operator : plugin.getVendingOperators()) {
			if (!operator.getPlayer().equals(player))
				continue;

			return operator;
		}
		for (NPCOperator operator : plugin.getNpcOperators()) {
			if (!operator.getPlayer().equals(player))
				continue;
			
			return operator;
		}
		return null;
	}
}
