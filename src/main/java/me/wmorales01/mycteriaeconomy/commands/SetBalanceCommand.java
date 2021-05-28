package me.wmorales01.mycteriaeconomy.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.EconomyPlayer;
import me.wmorales01.mycteriaeconomy.util.Messager;
import me.wmorales01.mycteriaeconomy.util.Parser;

public class SetBalanceCommand implements CommandExecutor {
	public MycteriaEconomy plugin;

	public SetBalanceCommand(MycteriaEconomy instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("setbalance"))
			return true;
		if (!sender.hasPermission("economyplugin.setbalance")) {
			Messager.sendNoPermissionMessage(sender);
			return true;
		}
		if (args.length < 2) {
			Messager.sendMessage(sender, "&cUsage: &l/setbalance <Player> <Amount>");
			return true;
		}
		Player receiver = Bukkit.getPlayer(args[0]);
		if (receiver == null) {
			Messager.sendMessage(sender, "&cPlayer not found.");
			return true;
		}
		EconomyPlayer ecoPlayer = EconomyPlayer.fromPlayer(receiver);
		if (ecoPlayer == null) {
			ecoPlayer = new EconomyPlayer(receiver);
			plugin.getEconomyPlayers().add(ecoPlayer);
		}
		Integer amount = Parser.getNumber(sender, args[1]);
		ecoPlayer.setBankBalance(amount);
		
		Messager.sendMessage(sender, "&a&l" + receiver.getDisplayName() + " &areceived &l" + amount + "$&a.");
		return true;
	}
}
