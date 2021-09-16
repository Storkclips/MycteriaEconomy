package me.wmorales01.mycteriaeconomy.commands;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CommandCompleter implements TabCompleter {
    private final MycteriaEconomy plugin;

    public CommandCompleter(MycteriaEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        ArrayList<String> arguments = new ArrayList<>();
        String typedCommand = cmd.getName();
        Map<String, ? extends AbstractSubcommand> subcommandList;
        if (typedCommand.equalsIgnoreCase("economy")) {
            subcommandList = plugin.getEconomySubcommands();
        } else if (typedCommand.equalsIgnoreCase("npcshop")) {
            subcommandList = plugin.getNpcSubcommands();
        } else {
            return arguments;
        }
        for (AbstractSubcommand subcommand : subcommandList.values()) {
            if (!sender.hasPermission(subcommand.getPermission())) continue;

            arguments.add(subcommand.getName());
        }
        AbstractSubcommand subcommand = subcommandList.get(args[0]);
        if (args.length > 1 && (subcommand == null || !sender.hasPermission(subcommand.getPermission()))) {
            return arguments;
        }
        if (args.length < 2) {
            return getCompletion(arguments, args, 0);
        } else if (args.length < 3) {
            arguments.clear();
            switch (subcommand.getName()) {
                case "balance":
                    if (sender.hasPermission("mycteriaeconomy.balance.view")) {
                        arguments.add("view");
                    }
                    if (sender.hasPermission("mycteriaeconomy.balance.set")) {
                        arguments.add("set");
                    }
                    if (sender.hasPermission("mycteriaeconomy.balance.change")) {
                        arguments.addAll(Arrays.asList("add", "remove"));
                    }
                    return getCompletion(arguments, args, 1);
            }
        } else if (args.length < 4) {
            arguments.clear();
            switch (subcommand.getName()) {
                case "balance":
                    if (!sender.hasPermission("mycteriaeconomy.balance.view") && !sender.hasPermission("mycteriaeconomy.balance.set")
                            && !sender.hasPermission("mycteriaeconomy.balance.change")) return arguments;
                    return null;
            }
        }
        arguments.clear();
        return arguments;
    }

    private ArrayList<String> getCompletion(ArrayList<String> arguments, String[] args, int index) {
        ArrayList<String> results = new ArrayList<>();
        for (String argument : arguments) {
            if (!argument.toLowerCase().startsWith(args[index].toLowerCase()))
                continue;

            results.add(argument);
        }
        return results;
    }
}
