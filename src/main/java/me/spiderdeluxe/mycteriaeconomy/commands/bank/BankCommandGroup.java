package me.spiderdeluxe.mycteriaeconomy.commands.bank;

import org.mineacademy.fo.command.SimpleCommandGroup;

public class BankCommandGroup extends SimpleCommandGroup {

    @Override
    protected void registerSubcommands() {
        
        registerSubcommand(new BankCommandEdit(this));
        registerSubcommand(new BankCommandMenu(this));
        registerSubcommand(new BankCommandTools(this));
        registerSubcommand(new BankCommandCreate(this));
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
