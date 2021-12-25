package me.spiderdeluxe.mycteriaeconomy.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SFXManager {

    public static void playSuccessSound(final CommandSender sender) {
        playPlayerSound(sender, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
    }

    public static void playErrorSound(final CommandSender sender) {
        playPlayerSound(sender, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 2);
    }

    public static void playPlayerSound(final CommandSender sender, final Sound sound, final float volume, final float pitch) {
        if (!(sender instanceof Player)) return;

        final Player player = (Player) sender;
        final Location location = player.getLocation();
        player.playSound(location, sound, volume, pitch);
    }

    public static void playWorldSound(final Location location, final Sound sound, final float volume, final float pitch) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }
}
