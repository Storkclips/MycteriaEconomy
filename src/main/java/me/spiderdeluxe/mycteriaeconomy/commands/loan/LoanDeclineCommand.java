package me.spiderdeluxe.mycteriaeconomy.commands.loan;

import me.spiderdeluxe.mycteriaeconomy.models.bank.BaseBank;
import me.spiderdeluxe.mycteriaeconomy.models.bank.Loan;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class LoanDeclineCommand extends SimpleSubCommand {

    public LoanDeclineCommand(final SimpleCommandGroup parent) {
        super(parent, "decline");

        setDescription("Use this command to decline a propose of loan");
        setPermission("mycteriaeconomy.loan");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        final Player borrower = getPlayer();


        //Check proposals
        checkBoolean(Loan.hasLendRequest(borrower), "You don't have any loan proposals.");

        checkBoolean(BaseBank.isWithin(borrower.getLocation()), "You are not within the bank area, so you cannot take this action.");


        //Decline lend
        Loan.declineLendPropose(borrower);

    }


}
