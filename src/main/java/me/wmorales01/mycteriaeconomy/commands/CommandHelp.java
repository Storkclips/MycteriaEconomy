package me.wmorales01.mycteriaeconomy.commands;

import me.wmorales01.mycteriaeconomy.util.Messager;
import org.bukkit.command.CommandSender;

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
