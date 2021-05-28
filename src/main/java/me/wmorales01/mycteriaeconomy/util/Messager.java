package me.wmorales01.mycteriaeconomy.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Messager {
	
	public static void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public static void sendSuccessMessage(CommandSender sender, String message) {
		sendMessage(sender, message);
		SFXManager.playSuccessSound(sender);
	}
	
	public static void sendErrorMessage(CommandSender sender, String message) {
		sendMessage(sender, message);
		SFXManager.playErrorSound(sender);
	}
	
	public static void sendHelpMessage(CommandSender sender) {
		String finalMessage = "&lCommands\n";
		
//		Iterator<TemplateCommand> iterator = plugin.getSubcommands().values().iterator();
//		
//		while (iterator.hasNext()) {
//			TemplateCommand subcommand = iterator.next();
//			if (!sender.hasPermission(subcommand.getPermission()))
//				continue;
//			
//			finalMessage += "&" + subcommand.getUsageMessage() + " &- &" + subcommand.getHelpMessage();
//			if (iterator.hasNext())
//				finalMessage += "\n";
//			
//		}
		
		Messager.sendSuccessMessage(sender, finalMessage);
	}
	
	public static void sendNoPermissionMessage(CommandSender sender) {
		sendErrorMessage(sender, "&cYou do not have permissions to use this command!");
	}
}
