package me.wmorales01.mycteriaeconomy.commands.economy;

import me.wmorales01.mycteriaeconomy.util.BalanceUtil;
import me.wmorales01.mycteriaeconomy.util.Messager;
import me.wmorales01.mycteriaeconomy.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyCommandCash extends EconomyCommand {

    public EconomyCommandCash() {
        setName("cash");
        setInfoMessage("Displays how much cash you have on your inventory.");
        setPermission("mycteriaeconomy.cash");
        setUsageMessage("/economy cash");
        setArgumentLength(1);
        setPlayerCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        double totalCashBalance = BalanceUtil.computeInventoryBalance(player.getInventory());
        Messager.sendSuccessMessage(player, "&eYou currently have &3&l$" + StringUtil.roundNumber(totalCashBalance, 2) +
                " &ein cash.");
    }
}
