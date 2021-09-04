package me.wmorales01.mycteriaeconomy.commands;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.util.Messager;
import org.bukkit.command.CommandSender;

public class CommandReload extends NPCCommand {
    private MycteriaEconomy plugin;

    public CommandReload(MycteriaEconomy plugin) {
        this.plugin = plugin;

        setName("reload");
        setInfoMessage("Reloads the plugin's config.");
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
