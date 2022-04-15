package me.spiderdeluxe.mycteriaeconomy.commands.business;

import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.models.account.BaseAccount;
import me.spiderdeluxe.mycteriaeconomy.models.account.BusinessAccount;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.ArrayList;
import java.util.List;

public class BusinessCommandCreate extends SimpleSubCommand {

    public BusinessCommandCreate(final SimpleCommandGroup parent) {
        super(parent, "create");

        setDescription("Create a business account.");
        setPermission("mycteriaeconomy.business");
        setMinArguments(2);
        setUsage("<countNumber> <player>");
    }

    @Override
    public void onCommand() {
        checkConsole();

        final Player player = findPlayer(args[1]);

        //Setup player
        checkNotNull(player, "No player named " + args[1] + " exists or isn't online");
        assert player != null;
        checkBoolean(player.isOnline(), "No player named " + args[1] + " isn't online");


        final EconomyPlayer ecoPlayer = EconomyPlayer.from(player);


        checkBoolean(ecoPlayer.getBusinessAccount().size() <= 2, "The player already has the maximum number of business accounts you can have");

        checkBoolean(Valid.isInteger(args[0]), "You must write down a number to identify your bank account.");
        final BaseAccount senderCount = BaseAccount.findByAccount(Integer.parseInt(args[0]));
        checkNotNull(senderCount, "You does not currently have an active bank account with id: " + args[0]);
        assert senderCount != null;
        checkBoolean(senderCount.isOwner(getPlayer()), "You are not the owner of this account.");


        checkBoolean(senderCount.getBalance() > Settings.General.BUSINESS_ACCOUNT_PRICE, "You don't have enough money"
                + " (" + senderCount.getBalance() + "/" + Settings.General.BUSINESS_ACCOUNT_PRICE + ")"
                + "  to open this account for " + player.getName());

        senderCount.decreaseBalance(Settings.General.BUSINESS_ACCOUNT_PRICE);

        ecoPlayer.addAccount(new BusinessAccount(player));

        tellSuccess("You have successfully created a business account in the name of " + player.getName());
        Messenger.success(player, getPlayer().getName() + " created a business account in your name");
    }

    @Override
    protected List<String> tabComplete() {

        switch (args.length) {
            case 1 -> {

                final Player player = getPlayer();

                final EconomyPlayer economyPlayer = EconomyPlayer.from(player);

                return completeLastWord(economyPlayer.getCountsNumbers());
            }
            case 2 -> {
                return completeLastWordPlayerNames();
            }
        }
        return new ArrayList<>();
    }

}
