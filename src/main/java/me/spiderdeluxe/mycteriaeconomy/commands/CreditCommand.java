package me.spiderdeluxe.mycteriaeconomy.commands;

import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.models.account.BaseAccount;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.command.SimpleCommand;

import java.util.List;

/**
 * @author SpiderDeluxe
 * This command is used to se your credit.
 */
public class CreditCommand extends SimpleCommand {

    public CreditCommand() {
        super("credit");
        setDescription("Use this command to see your credit.");
        setPermission("mycteriaeconomy.credit");
        setUsage("<count>");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        final Player player = getPlayer();
        final String countNumber = args[0];

        checkBoolean(Valid.isInteger(countNumber), "You must write down a number to identify your bank account.");
        final BaseAccount senderCount = BaseAccount.findByAccount(Integer.parseInt(countNumber));
        checkNotNull(senderCount, "You does not currently have an active bank account with id: " + countNumber);
        assert senderCount != null;
        checkBoolean(senderCount.isOwner(player), "You are not the owner of this account.");

        tellInfo("There is $" + senderCount.getBalance() + " in this account");
    }

    @Override
    protected List<String> tabComplete() {
        final Player player = getPlayer();
        final EconomyPlayer economyPlayer = EconomyPlayer.from(player);
        return completeLastWord(economyPlayer.getCountsNumbers());
    }
}
