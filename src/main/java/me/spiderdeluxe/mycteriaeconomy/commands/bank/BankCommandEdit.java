package me.spiderdeluxe.mycteriaeconomy.commands.bank;

import me.spiderdeluxe.mycteriaeconomy.models.bank.Branch;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class BankCommandEdit extends SimpleSubCommand {

    public BankCommandEdit(final SimpleCommandGroup parent) {
        super(parent, "edit");

        setDescription("Use this command to stop editing a bank's branch");
        setPermission("mycteriaeconomy.bank.edit");
    }

    @Override
    protected void onCommand() {
        checkConsole();

        checkBoolean(Branch.isEditingPlayer(getPlayer()), "You are not modifying any branch of a bank");
        final Branch branch = Branch.getEditingBranch(getPlayer());

        assert branch != null;
        branch.setEditingPlayer(null);
        tellSuccess("You have successfully disabled bank's edit mode.");
    }


}
