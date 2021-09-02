package me.wmorales01.mycteriaeconomy.util;

import org.bukkit.command.CommandSender;

public class Parser {

    public static Integer getNumber(CommandSender sender, String input) {
        int number = 0;
        try {
            number = Integer.parseInt(input);
        } catch (Exception e) {
            return null;
        }

        return number;
    }
}
