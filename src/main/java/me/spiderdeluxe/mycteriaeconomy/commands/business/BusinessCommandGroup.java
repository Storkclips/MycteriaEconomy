package me.spiderdeluxe.mycteriaeconomy.commands.business;

import org.mineacademy.fo.command.SimpleCommandGroup;

public class BusinessCommandGroup extends SimpleCommandGroup {

    @Override
    protected void registerSubcommands() {
        registerSubcommand(new BusinessCommandCreate(this));
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
