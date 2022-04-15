package me.spiderdeluxe.mycteriaeconomy.util;

import org.bukkit.ChatColor;

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

}
