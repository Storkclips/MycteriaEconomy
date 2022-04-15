package me.spiderdeluxe.mycteriaeconomy.commands.work;

import me.spiderdeluxe.mycteriaeconomy.models.work.BaseWork;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class WorkCommandDecline extends SimpleSubCommand {

    public WorkCommandDecline(final SimpleCommandGroup parent) {
        super(parent, "decline");

        setDescription("Use this command to decline a propose of work");
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
        BaseWork.declineInvite(employer);

    }


}
