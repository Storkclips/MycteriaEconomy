package me.spiderdeluxe.mycteriaeconomy.commands.loan;

import me.spiderdeluxe.mycteriaeconomy.models.bank.BaseBank;
import me.spiderdeluxe.mycteriaeconomy.models.bank.Loan;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class LoanViewCommand extends SimpleSubCommand {

    public LoanViewCommand(final SimpleCommandGroup parent) {
        super(parent, "view");

        setDescription("Use this command to view a propose of loan");
        setPermission("mycteriaeconomy.loan");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        final Player player = getPlayer();


        //Check proposals
        checkBoolean(Loan.isBorrower(player) || Loan.isLender(player), "You do not currently have any active loans");
        checkBoolean(BaseBank.isWithin(player.getLocation()), "You are not within the bank area, so you cannot take this action.");


        if (Loan.isBorrower(player)) {
            tellNoPrefix("&7" + Common.chatLineSmooth());
            tellNoPrefix("    &c&lLoans received   ");
            tellNoPrefix("&7" + Common.chatLineSmooth());
            tellNoPrefix("&7  lender  |   remained    |   total");
            for (final Loan loan : Loan.getBorrowerLoans(player)) {
                tellNoPrefix("  " + loan.getLenderCount().getOwner().getName() + "  :  (" + loan.getPayBackMoney() + "|" + loan.getLoanedMoney() + ")");
            }
        }


        if (Loan.isLender(player)) {
            tellNoPrefix("&7" + Common.chatLineSmooth());
            tellNoPrefix("   &b&lSubmitted Loans");
            tellNoPrefix("&7" + Common.chatLineSmooth());
            tellNoPrefix("&7  borrower  |   remained    |   total");
            for (final Loan loan : Loan.getLenderLoans(player)) {
                tellNoPrefix(" " + loan.getBorrowerCount().getOwner().getName() + "  :  (" + loan.getPayBackMoney() + "|" + loan.getLoanedMoney() + ")");
            }
        }
    }
}