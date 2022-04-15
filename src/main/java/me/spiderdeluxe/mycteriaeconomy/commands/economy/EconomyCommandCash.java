package me.spiderdeluxe.mycteriaeconomy.commands.economy;

import me.spiderdeluxe.mycteriaeconomy.util.BalanceUtil;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import me.spiderdeluxe.mycteriaeconomy.util.StringUtil;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

/**
 * @author  SpiderDeluxe
 * This command is used to display how much cash you have on your inventory.
 */
public class EconomyCommandCash extends SimpleSubCommand {

    public EconomyCommandCash(final SimpleCommandGroup parent) {
        super(parent, "cash");

        setDescription("Displays how much cash you have on your inventory.");
        setPermission("mycteriaeconomy.cash");
    }

    @Override
    public void onCommand() {
        checkConsole();

        final Player player = (Player) sender;
        final double totalCashBalance = BalanceUtil.computeInventoryBalance(player.getInventory());
        Messager.sendSuccessMessage(player, "&eYou currently have &3&l$" + StringUtil.roundNumber(totalCashBalance, 2) +
                " &ein cash.");
    }
}
