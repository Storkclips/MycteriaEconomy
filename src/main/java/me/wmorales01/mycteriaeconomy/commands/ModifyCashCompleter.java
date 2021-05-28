package me.wmorales01.mycteriaeconomy.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ModifyCashCompleter implements TabCompleter {

	private ArrayList<String> arguments = new ArrayList<String>();

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length >= 0 && args.length < 2) {
			return null;
		}
		
		return arguments;
	}
}
