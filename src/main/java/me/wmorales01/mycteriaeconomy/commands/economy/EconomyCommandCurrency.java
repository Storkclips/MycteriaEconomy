package me.wmorales01.mycteriaeconomy.commands.economy;

import me.wmorales01.mycteriaeconomy.inventories.CurrencyGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyCommandCurrency extends EconomyCommand {

    public EconomyCommandCurrency() {
        setName("currency");
        setInfoMessage("Opens a GUI with all the available currency items.");
        setPermission("mycteriaeconomy.currency");
        setUsageMessage("/economy currency");
        setArgumentLength(1);
        setPlayerCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        player.openInventory(new CurrencyGUI().getGUI());
    }
}
