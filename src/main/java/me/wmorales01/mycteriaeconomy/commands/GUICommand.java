package me.wmorales01.mycteriaeconomy.commands;

import me.wmorales01.mycteriaeconomy.inventories.CreativeGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GUICommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("gui"))
            return true;
        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;
        player.openInventory(CreativeGUI.getCreativeGUI());

        return false;
    }
}
