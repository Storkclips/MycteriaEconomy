package me.wmorales01.mycteriaeconomy.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandUtil {

    /**
     * Attemps to find the target of a command from the passed String[] based on the passed index.
     *
     * @param args  arguments used on the command.
     * @param index index where the command target will be searched.
     * @return the Player instance of the command's target. Null if it couldn't find the player.
     */
    public static Player getTargetFromArgs(String[] args, int index) {
        String targetName = args[index];
        return Bukkit.getPlayerExact(targetName);
    }
}
