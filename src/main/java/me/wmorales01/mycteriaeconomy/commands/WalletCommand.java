package me.wmorales01.mycteriaeconomy.commands;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.Wallet;
import me.wmorales01.mycteriaeconomy.util.Messager;
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
        if (!cmd.getName().equalsIgnoreCase("wallet")) return true;
        if (!(sender instanceof Player)) {
            Messager.sendErrorMessage(sender, "&cConsoles can't have wallets.");
            return true;
        }
        if (!sender.hasPermission("economyplugin.wallet")) {
            Messager.sendNoPermissionMessage(sender);
            return true;
        }
        Player player = (Player) sender;
        Inventory inventory = player.getInventory();
        Wallet wallet = new Wallet();
        if (inventory.firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), wallet.getItemStack());
        } else {
            inventory.addItem(wallet.getItemStack());
        }
        Messager.sendSuccessMessage(player, "&6You received a new wallet!");
        return true;
    }
}
