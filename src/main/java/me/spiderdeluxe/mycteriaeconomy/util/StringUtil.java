package me.spiderdeluxe.mycteriaeconomy.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;

public class StringUtil {

    public static String formatColor(final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String roundNumber(final double number, final int decimalPlaces) {
        final StringBuilder builder = new StringBuilder("#.");
        builder.append("#".repeat(Math.max(0, decimalPlaces)));
        return new DecimalFormat(builder.toString()).format(number);
    }

    public static Double parseDouble(final CommandSender sender, final String string) {
        try {
            return Double.parseDouble(string);
        } catch (final Exception e) {
            Messager.sendErrorMessage(sender, "&cYou must enter a numeric value.");
            return null;
        }
    }
}
