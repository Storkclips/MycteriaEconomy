package me.spiderdeluxe.mycteriaeconomy.commands.work;

import me.spiderdeluxe.mycteriaeconomy.models.work.BaseWork;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class WorkCommandDelete extends SimpleSubCommand {

    public WorkCommandDelete(final SimpleCommandGroup parent) {
        super(parent, "delete");

        setDescription("Use this command to create a work");
        setPermission("mycteriaeconomy.work.delete");
    }

    @Override
    protected void onCommand() {
        checkConsole();

        checkBoolean(BaseWork.hasWork(getPlayer()), "You haven't a job");
        checkBoolean(BaseWork.hasWork(getPlayer()), "You aren't the job's owner");

        final BaseWork work = new BaseWork(getPlayer());
        new WorkCreatePrompt(work).show(getPlayer());
    }


}
