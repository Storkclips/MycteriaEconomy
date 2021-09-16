package me.wmorales01.mycteriaeconomy.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;

public class StringUtil {

    public static String formatColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    public static String roundNumber(double number, int decimalPlaces) {
        StringBuilder builder = new StringBuilder("#.");
        for (int i = 0; i < decimalPlaces; i++) {
            builder.append("#");
        }
        return new DecimalFormat(builder.toString()).format(number);
    }

    public static String formatEnum(Enum<?> enumeration) {
        String[] split = enumeration.name().split("_");
        if (split.length > 0) {
            for (int i = 0; i < split.length; i++) {
                String word = split[i];
                if (word.equalsIgnoreCase("of") || word.equalsIgnoreCase("the")) {
                    split[i] = word.toLowerCase();
                    continue;
                }
                split[i] = capitalize(word);
            }
            return String.join(" ", split);
        }
        return capitalize(enumeration.name());
    }

    public static Integer parseInteger(CommandSender sender, String string) {
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            Messager.sendErrorMessage(sender, "&cYou must enter a numeric value with no decimals.");
            return null;
        }
    }

    public static Double parseDouble(CommandSender sender, String string) {
        Double number;
        try {
            return Double.parseDouble(string);
        } catch (Exception e) {
            Messager.sendErrorMessage(sender, "&cYou must enter a numeric value.");
            return null;
        }
    }
}
