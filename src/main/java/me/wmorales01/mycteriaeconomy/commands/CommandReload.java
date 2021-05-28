package me.wmorales01.mycteriaeconomy.commands;

import org.bukkit.command.CommandSender;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.util.Messager;

public class CommandReload extends NPCCommand {
	private MycteriaEconomy plugin;

	public CommandReload(MycteriaEconomy plugin) {
		this.plugin = plugin;

		setName("reload");
		setHelpMessage("Reloads the plugin's config.");
		setPermission(".reload");
		setUsageMessage("/ reload");
		setArgumentLength(1);
		setUniversalCommand(true);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		plugin.reloadConfig();
		Messager.sendMessage(sender, "&aConfig reloaded.");
	}
}
