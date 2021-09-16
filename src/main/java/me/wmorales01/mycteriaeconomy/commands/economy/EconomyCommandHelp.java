package me.wmorales01.mycteriaeconomy.commands.economy;

import me.wmorales01.mycteriaeconomy.util.Messager;
import org.bukkit.command.CommandSender;

public class EconomyCommandHelp extends EconomyCommand {

    public EconomyCommandHelp() {
        setName("help");
        setInfoMessage("Displays this list.");
        setPermission("mycteriaeconomy.help");
        setUsageMessage("/economy help");
        setArgumentLength(1);
        setUniversalCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messager.sendHelpMessage(sender);
    }
}
