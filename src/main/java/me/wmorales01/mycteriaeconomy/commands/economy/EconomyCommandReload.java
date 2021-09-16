package me.wmorales01.mycteriaeconomy.commands.economy;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.util.Messager;
import org.bukkit.command.CommandSender;

public class EconomyCommandReload extends EconomyCommand {
    private final MycteriaEconomy plugin;

    public EconomyCommandReload(MycteriaEconomy plugin) {
        this.plugin = plugin;

        setName("reload");
        setInfoMessage("Reloads the plugin's config.");
        setPermission("mycteriaeconomy.reload");
        setUsageMessage("/economy reload");
        setArgumentLength(1);
        setUniversalCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.reloadConfig();
        Messager.sendSuccessMessage(sender, "&aConfig reloaded.");
    }
}
