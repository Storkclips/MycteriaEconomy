package me.spiderdeluxe.mycteriaeconomy.commands.loan;

import me.spiderdeluxe.mycteriaeconomy.models.bank.BaseBank;
import me.spiderdeluxe.mycteriaeconomy.models.bank.Loan;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class LoanPayCommand extends SimpleSubCommand {
    public LoanPayCommand(final SimpleCommandGroup parent) {
        super(parent, "pay");

        setDescription("Use this command to pay a part of your loan");
        setPermission("mycteriaeconomy.loan");
        setUsage("<lender> <amount>");
        setMinArguments(1);
    }

    @Override
    protected void onCommand() {
        checkConsole();
        final Player borrower = getPlayer();

        //Setup lender
        final Player lender = findPlayer(args[0]);

        checkNotNull(lender, "No player named " + args[0] + " exists or isn't online");
        assert lender != null;
        checkBoolean(lender.isOnline(), "No player named " + args[0] + " isn't online");
        checkNotNull(lender == borrower, "You can't lend yourself money");

        //Check proposals
        checkBoolean(Loan.hasLend(lender, borrower), lender.getName() + " didn't make you any loan");

        final Loan loan = Loan.findByPlayers(lender, borrower);

        //Setup amount
        checkBoolean(Valid.isInteger(args[1]), "You must write down a number to specify amount.");
        final int amount = Integer.parseInt(args[1]);

        assert loan != null;
        checkBoolean(loan.getBorrowerCount().getBalance() >= amount, "You cannot pay more than you have (your balance is: " + loan.getBorrowerCount().getBalance() + ")");
        checkBoolean(loan.getPayBackMoney() >= amount, "You cannot pay more than your remained debt (your remained debt is: " + loan.getPayBackMoney() + "/" + loan.getLoanedMoney() + ")");

        checkBoolean(BaseBank.isWithin(borrower.getLocation()), "You are not within the bank area, so you cannot take this action.");

        loan.payLoan(amount, borrower.getLocation());
    }
}
