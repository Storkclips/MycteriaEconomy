package me.spiderdeluxe.mycteriaeconomy.commands.bank;

import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.models.account.BaseAccount;
import me.spiderdeluxe.mycteriaeconomy.models.bank.CommunityBank;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.ArrayList;
import java.util.List;

public class BankCommandCreate extends SimpleSubCommand {

    public BankCommandCreate(final SimpleCommandGroup parent) {
        super(parent, "create");

        setDescription("Use this command to create a community bank");
        setPermission("mycteriaeconomy.bank.create");
        setUsage("<countNumber> <name>");
        setMinArguments(2);
    }

    @Override
    protected void onCommand() {
        checkConsole();

        final Player player = getPlayer();
        final String countNumber = args[0];
        final String name = args[1];

        checkBoolean(!CommunityBank.alreadyExist(name), "Already exits a community bank called: " + name);
        checkBoolean(!CommunityBank.hasBank(player), "You have already a bank!");


        checkBoolean(Valid.isInteger(countNumber), "You must write down a number to identify your bank account.");
        final BaseAccount senderCount = BaseAccount.findByAccount(Integer.parseInt(countNumber));
        checkNotNull(senderCount, "You does not currently have an active bank account with id: " + countNumber);
        assert senderCount != null;
        checkBoolean(senderCount.isOwner(getPlayer()), "You are not the owner of this account.");


        checkBoolean(senderCount.getBalance() > Settings.General.BUSINESS_ACCOUNT_PRICE, "You don't have enough money"
                + " (" + senderCount.getBalance() + "/" + Settings.General.BUSINESS_ACCOUNT_PRICE + ")"
                + "  to open this community bank");

        senderCount.decreaseBalance(Settings.General.BUSINESS_ACCOUNT_PRICE);
        new CommunityBank(name, player);

        tellSuccess("You have successfully created a community bank called: " + name);
    }

    @Override
    protected List<String> tabComplete() {

        if (args.length == 1) {
            final Player player = getPlayer();
            final EconomyPlayer economyPlayer = EconomyPlayer.from(player);
            return completeLastWord(economyPlayer.getCountsNumbers());
        }
        return new ArrayList<>();
    }
}
