package me.wmorales01.mycteriaeconomy.commands;

import org.bukkit.command.CommandSender;

public abstract class NPCCommand {
	private String name;
	private String helpMessage;
	private String permission;
	private String usageMessage;
	private int argumentLength;
	boolean isConsoleCommand;
	boolean isPlayerCommand;
	boolean isUniversalCommand;

	public abstract void execute(CommandSender sender, String[] args);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHelpMessage() {
		return helpMessage;
	}

	public void setHelpMessage(String helpMessage) {
		this.helpMessage = helpMessage;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getUsageMessage() {
		return usageMessage;
	}

	public void setUsageMessage(String usageMessage) {
		this.usageMessage = usageMessage;
	}

	public int getArgumentLength() {
		return argumentLength;
	}

	public void setArgumentLength(int argumentLength) {
		this.argumentLength = argumentLength;
	}

	public boolean isConsoleCommand() {
		return isConsoleCommand;
	}

	public void setConsoleCommand(boolean isConsoleCommand) {
		this.isConsoleCommand = isConsoleCommand;
	}

	public boolean isPlayerCommand() {
		return isPlayerCommand;
	}

	public void setPlayerCommand(boolean isPlayerCommand) {
		this.isPlayerCommand = isPlayerCommand;
	}

	public boolean isUniversalCommand() {
		return isUniversalCommand;
	}

	public void setUniversalCommand(boolean isUniversalCommand) {
		this.isUniversalCommand = isUniversalCommand;
	}
}
