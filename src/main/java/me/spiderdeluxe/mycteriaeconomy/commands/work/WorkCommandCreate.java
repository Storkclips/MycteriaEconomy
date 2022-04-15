package me.spiderdeluxe.mycteriaeconomy.commands.work;

import me.spiderdeluxe.mycteriaeconomy.models.work.BaseWork;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class WorkCommandCreate extends SimpleSubCommand {

    public WorkCommandCreate(final SimpleCommandGroup parent) {
        super(parent, "create");

        setDescription("Use this command to create a work");
        setPermission("mycteriaeconomy.work.create");
    }

    @Override
    protected void onCommand() {
        checkConsole();

        checkBoolean(!BaseWork.hasWork(getPlayer()), "You have already a job!");

        final BaseWork work = new BaseWork(getPlayer());
        new WorkCreatePrompt(work).show(getPlayer());
    }


}
