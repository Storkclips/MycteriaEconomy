package me.spiderdeluxe.mycteriaeconomy.commands.work;

import me.spiderdeluxe.mycteriaeconomy.models.work.BaseWork;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class WorkCommandInvite extends SimpleSubCommand {

    public WorkCommandInvite(final SimpleCommandGroup parent) {
        super(parent, "invite");

        setDescription("Use this command to invite a player");
        setPermission("mycteriaeconomy.work.invite");
        setMinArguments(1);
        setUsage("<player>");
    }

    @Override
    protected void onCommand() {
        checkConsole();

        final Player administrator = getPlayer();

        checkBoolean(BaseWork.hasWork(administrator), "You haven't a job");
        checkBoolean(BaseWork.hasWork(administrator), "You aren't the job's owner");

        final BaseWork work = BaseWork.fromAdministrator(administrator);


        //Setup employer

        final Player employer = findPlayer(args[0]);

        checkNotNull(employer, "No player named " + args[0] + " exists or isn't online");
        assert employer != null;
        checkBoolean(employer.isOnline(), "No player named " + args[0] + " isn't online");
        checkBoolean(employer != administrator, "You can't invite yourself!");
        checkBoolean(BaseWork.hasWork(employer), " has already a job!");
        checkBoolean(!BaseWork.hasInvite(employer), employer.getName() + " has already a work propose!");


        work.invitePlayer(employer);
        Messenger.success(administrator, "You have successfully invited him into your company");
    }


}
