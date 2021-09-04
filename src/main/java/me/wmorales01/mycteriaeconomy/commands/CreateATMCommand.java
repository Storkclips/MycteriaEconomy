package me.wmorales01.mycteriaeconomy.commands;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.ATM;
import me.wmorales01.mycteriaeconomy.util.Messager;
import me.wmorales01.mycteriaeconomy.util.SFXManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class CreateATMCommand implements CommandExecutor {
    private final MycteriaEconomy plugin;

    public CreateATMCommand(MycteriaEconomy instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("atm")) return true;
        if (!(sender instanceof Player)) {
            Messager.sendErrorMessage(sender, "&cNot available for consoles.");
            return true;
        }
        if (!sender.hasPermission("economyplugin.createatm")) {
            Messager.sendNoPermissionMessage(sender);
            return true;
        }
        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();
        if (inventory.firstEmpty() == -1) {
            Messager.sendErrorMessage(player, "&cFree a space in your inventory to run this command.");
            return true;
        }
        inventory.addItem(ATM.getItemStack());
        Messager.sendMessage(player, "&ePlace down this item to create an ATM.");
        SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 0.6F, 1.4F);
        return true;
    }
}
