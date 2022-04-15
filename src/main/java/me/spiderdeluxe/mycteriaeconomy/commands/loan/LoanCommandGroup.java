package me.spiderdeluxe.mycteriaeconomy.commands.loan;

import org.mineacademy.fo.command.SimpleCommandGroup;

public class LoanCommandGroup extends SimpleCommandGroup {

    @Override
    protected void registerSubcommands() {
        registerSubcommand(new LoanProposeCommand(this));

        registerSubcommand(new LoanPayCommand(this));
        registerSubcommand(new LoanViewCommand(this));

        registerSubcommand(new LoanAcceptCommand(this));
        registerSubcommand(new LoanDeclineCommand(this));

        registerSubcommand(new LoanWithdrawCommand(this));
    }

    @Override
    protected String getCredits() {
        return "Plugin that manages the economy of server.";
    }

    @Override
    protected String getHeaderPrefix() {
        return "{#fe7916}&l";
    }

}
