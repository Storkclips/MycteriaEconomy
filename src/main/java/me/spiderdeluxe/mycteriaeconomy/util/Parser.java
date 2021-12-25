package me.spiderdeluxe.mycteriaeconomy.util;

import org.bukkit.command.CommandSender;

public class Parser {

    public static Integer getNumber(final CommandSender sender, final String input) {
        int number = 0;
        try {
            number = Integer.parseInt(input);
        } catch (final Exception e) {
            return null;
        }

        return number;
    }
}
