package me.wmorales01.mycteriaeconomy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

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
