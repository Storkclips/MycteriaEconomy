package me.spiderdeluxe.mycteriaeconomy.commands.work;

import org.mineacademy.fo.command.SimpleCommandGroup;

public class WorkCommandGroup extends SimpleCommandGroup {

    @Override
    protected void registerSubcommands() {

        registerSubcommand(new WorkCommandCreate(this));
        registerSubcommand(new WorkCommandDelete(this));

        registerSubcommand(new WorkCommandDecline(this));
        registerSubcommand(new WorkCommandAccept(this));
        registerSubcommand(new WorkCommandInvite(this));
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
