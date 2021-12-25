package me.spiderdeluxe.mycteriaeconomy.util;

import org.bukkit.command.CommandSender;
import org.mineacademy.fo.Common;

public class Messager {

	public static void sendMessage(final CommandSender sender, final String message) {
		Common.tell(sender, Common.colorize(message));
	}

	public static void sendSuccessMessage(final CommandSender sender, final String message) {
		sendMessage(sender, message);
		SFXManager.playSuccessSound(sender);
	}

	public static void sendErrorMessage(final CommandSender sender, final String message) {
		sendMessage(sender, message);
		SFXManager.playErrorSound(sender);
	}

	public static void sendNoPermissionMessage(final CommandSender sender) {
		sendErrorMessage(sender, "&cYou do not have permissions to use this command!");
	}

	public static void sendPlayerNotFoundMessage(final CommandSender sender, final String playerName) {
		Messager.sendErrorMessage(sender, "&cPlayer &l" + playerName + " &cnot found.");
	}
}
