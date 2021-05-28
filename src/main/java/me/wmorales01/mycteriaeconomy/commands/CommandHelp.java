package me.wmorales01.mycteriaeconomy.commands;

import org.bukkit.command.CommandSender;

import me.wmorales01.mycteriaeconomy.util.Messager;

public class CommandHelp extends NPCCommand {
	public CommandHelp() {
		setName("help");
		setHelpMessage("Displays this list.");
		setPermission(".help");
		setUsageMessage("/ help");
		setArgumentLength(1);
		setUniversalCommand(true);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Messager.sendHelpMessage(sender);
	}
}
