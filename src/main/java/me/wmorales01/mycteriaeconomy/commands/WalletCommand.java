package me.wmorales01.mycteriaeconomy.commands;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.Wallet;
import me.wmorales01.mycteriaeconomy.util.Messager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class WalletCommand implements CommandExecutor {
    private MycteriaEconomy plugin;

    public WalletCommand(MycteriaEconomy instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("wallet"))
            return true;
        if (!sender.hasPermission("economyplugin.wallet")) {
            Messager.sendNoPermissionMessage(sender);
            return true;
        }
        Player player = (Player) sender;
        Inventory inventory = player.getInventory();

        Wallet wallet = new Wallet();
        if (inventory.firstEmpty() == -1) {
            Location location = player.getLocation();
            location.getWorld().dropItemNaturally(location, wallet.getItemStack());
            return true;
        }
        inventory.addItem(wallet.getItemStack());

        plugin.addWallet(wallet);
        Messager.sendMessage(player, "&6You received a new wallet!");

        return true;
    }
}
