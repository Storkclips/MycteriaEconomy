package me.wmorales01.mycteriaeconomy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.ATM;
import me.wmorales01.mycteriaeconomy.util.Messager;

public class CreateATMCommand implements CommandExecutor {
	private MycteriaEconomy plugin;

	public CreateATMCommand(MycteriaEconomy instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("createatm"))
			return true;
		if (!(sender instanceof Player)) {
			Messager.sendMessage(sender, "&cNot available for consoles.");
			return true;
		}
		if (!sender.hasPermission("economyplugin.createatm")) {
			Messager.sendNoPermissionMessage(sender);
			return true;
		}

		Player player = (Player) sender;
		PlayerInventory inventory = player.getInventory();
		if (inventory.firstEmpty() == -1) {
			Messager.sendMessage(player, "&cFree a space in your inventory to run this command");
			return true;
		}
		inventory.addItem(ATM.getATMItem());
		plugin.getATMPlacers().add(player);
		return true;
	}
}
