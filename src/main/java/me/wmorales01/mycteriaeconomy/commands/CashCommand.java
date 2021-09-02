package me.wmorales01.mycteriaeconomy.commands;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.util.BalanceManager;
import me.wmorales01.mycteriaeconomy.util.Messager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CashCommand implements CommandExecutor {
    public MycteriaEconomy plugin;

    public CashCommand(MycteriaEconomy instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("cash"))
            return true;
        if (!(sender instanceof Player)) {
            Messager.sendMessage(sender, "&cNot available for consoles.");
            return true;
        }
        Player player = (Player) sender;
        double totalBalance = BalanceManager.getBalanceFromInventory(player.getInventory());
        DecimalFormat format = new DecimalFormat("###.##");
        Messager.sendMessage(player, "&6You currently have &a&l" + format.format(totalBalance) + "$&a.");
        return true;
    }
}
