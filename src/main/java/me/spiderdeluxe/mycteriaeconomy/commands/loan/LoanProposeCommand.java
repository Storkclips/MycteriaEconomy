package me.spiderdeluxe.mycteriaeconomy.commands.loan;

import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.models.account.BaseAccount;
import me.spiderdeluxe.mycteriaeconomy.models.bank.BaseBank;
import me.spiderdeluxe.mycteriaeconomy.models.bank.Loan;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.ArrayList;
import java.util.List;

public class LoanProposeCommand extends SimpleSubCommand {

    public LoanProposeCommand(final SimpleCommandGroup parent) {
        super(parent, "propose");

        setDescription("Use this command for give loan to a player");
        setPermission("mycteriaeconomy.loan");
        setUsage("<lenderCount> <player> <borrowerCount> <amount>");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        final Player lender = getPlayer();

        //Setup lenderCount
        checkBoolean(Valid.isInteger(args[2]), "You must write down a number to identify your bank account.");
        final BaseAccount lenderCount = BaseAccount.findByAccount(Integer.parseInt(args[0]));
        checkNotNull(lenderCount, "does not currently have an active bank account with id: " + args[0]);
        assert lenderCount != null;
        checkBoolean(lenderCount.isOwner(lender), "You are not the owner of this account, you cannot make loans to his name");


        final Player borrower = Bukkit.getPlayer(args[1]);

        //Setup borrower
        checkNotNull(borrower, "No player named " + args[1] + " exists or isn't online");
        assert borrower != null;
        checkBoolean(borrower.isOnline(), "No player named " + args[1] + " isn't online");
        checkBoolean(borrower != lender, "You can't lend yourself money");

        checkBoolean(!Loan.hasLendRequest(borrower), "He already has a loan proposal");


        //Setup borrowerCount
        checkBoolean(Valid.isInteger(args[2]), "You must write down a number to identify your bank account.");
        final BaseAccount borrowerCount = BaseAccount.findByAccount(Integer.parseInt(args[2]));
        checkNotNull(borrowerCount, "does not currently have an active bank account with id: " + args[2]);
        assert borrowerCount != null;
        checkBoolean(borrowerCount.isOwner(borrower), borrower.getName() + " are not the owner of this account, he cannot make loans to his name");

        checkBoolean(!Loan.hasLend(lenderCount, borrowerCount), "You cannot lend " + borrower.getName() + " because you're already giving him a loan!");


        //Setup amount
        checkBoolean(Valid.isInteger(args[3]), "You must write down a number to specify amount.");
        final int amount = Integer.parseInt(args[3]);

        checkBoolean(lenderCount.getBalance() >= amount, "You cannot lend more than you have (your balance is: " + lenderCount.getBalance() + ")");

        checkBoolean(BaseBank.isWithin(lender.getLocation()), "You are not within the bank area, so you cannot take this action.");


        Loan.offerLend(lenderCount, borrowerCount, amount);
    }


    @Override
    protected List<String> tabComplete() {

        switch (args.length) {
            case 1 -> {
                final EconomyPlayer economyPlayer = EconomyPlayer.from(getPlayer());

                return completeLastWord(economyPlayer.getCountsNumbers());
            }
            case 2 -> {
                return completeLastWordPlayerNames();
            }
            case 3 -> {

                final Player player = Bukkit.getPlayer(args[1]);

                if (player == null) return new ArrayList<>();

                final EconomyPlayer economyPlayer = EconomyPlayer.from(player);

                return completeLastWord(economyPlayer.getCountsNumbers());
            }
        }
        return new ArrayList<>();
    }

}
