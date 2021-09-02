package me.wmorales01.mycteriaeconomy.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;

public class StringUtil {

    public static String formatColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String roundNumber(double number, int decimalPlaces) {
        StringBuilder builder = new StringBuilder("#.");
        for (int i = 0; i < decimalPlaces; i++) {
            builder.append("#");
        }
        return new DecimalFormat(builder.toString()).format(number);
    }

    public static Integer getInteger(CommandSender sender, String string) {
        Integer number;
        try {
            number = Integer.parseInt(string);
        } catch (Exception e) {
            Messager.sendErrorMessage(sender, "&cYou must enter a numeric value with no decimals.");
            return null;
        }
        return number;
    }

    public static Double getDouble(CommandSender sender, String string) {
        Double number;
        try {
            number = Double.parseDouble(string);
        } catch (Exception e) {
            Messager.sendErrorMessage(sender, "&cYou must enter a numeric value.");
            return null;
        }
        return number;
    }
}
