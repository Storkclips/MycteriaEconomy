package me.spiderdeluxe.mycteriaeconomy.commands.work;

import me.spiderdeluxe.mycteriaeconomy.models.work.BaseWork;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class WorkCommandAccept extends SimpleSubCommand {

    public WorkCommandAccept(final SimpleCommandGroup parent) {
        super(parent, "accept");

        setDescription("Use this command to accept a propose of work");
        setPermission("mycteriaeconomy.work");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        final Player employer = getPlayer();


        //Check proposals
        checkBoolean(BaseWork.hasWork(employer), " has already a job!");
        checkBoolean(!BaseWork.hasInvite(employer), employer.getName() + " has already a work propose!");


        //Decline work
        BaseWork.acceptInvite(employer);

    }


}
