package me.spiderdeluxe.mycteriaeconomy.commands.loan;

import me.spiderdeluxe.mycteriaeconomy.models.bank.BaseBank;
import me.spiderdeluxe.mycteriaeconomy.models.bank.Loan;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class LoanAcceptCommand extends SimpleSubCommand {

    public LoanAcceptCommand(final SimpleCommandGroup parent) {
        super(parent, "accept");

        setDescription("Use this command to accept a propose of loan");
        setPermission("mycteriaeconomy.loan");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        final Player borrower = getPlayer();

        //Check proposals
        checkBoolean(Loan.hasLendRequest(borrower), "You don't have any loan proposals.");

        checkBoolean(BaseBank.isWithin(borrower.getLocation()), "You are not within the bank area, so you cannot take this action.");


        //Accept lend
        Loan.acceptLendPropose(borrower);
    }


}
