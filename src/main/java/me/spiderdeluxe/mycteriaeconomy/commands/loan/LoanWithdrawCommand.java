package me.spiderdeluxe.mycteriaeconomy.commands.loan;

import me.spiderdeluxe.mycteriaeconomy.models.bank.BaseBank;
import me.spiderdeluxe.mycteriaeconomy.models.bank.Loan;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class LoanWithdrawCommand extends SimpleSubCommand {

    public LoanWithdrawCommand(final SimpleCommandGroup parent) {
        super(parent, "withdraw");

        setDescription("Use this command to withdraw a propose of loan");
        setPermission("mycteriaeconomy.loan");
        setUsage("<lender>");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        final Player lender = getPlayer();

        //Setup borrower
        final Player borrower = findPlayer(args[0]);

        checkNotNull(borrower, "No player named " + args[0] + " exists or isn't online");
        assert borrower != null;
        checkBoolean(borrower.isOnline(), "No player named " + args[0] + " isn't online");
        checkBoolean(borrower != lender, "You can't lend yourself money");

        //Check proposals
        checkBoolean(Loan.hasLendRequest(borrower), "You didn't make " + borrower.getName() + " any loan proposals");

        checkBoolean(BaseBank.isWithin(lender.getLocation()), "You are not within the bank area, so you cannot take this action.");

        //Retire lend
        Loan.retireLendPropose(lender);

    }


}
