package me.spiderdeluxe.mycteriaeconomy.commands.bank;

import me.spiderdeluxe.mycteriaeconomy.tool.BankTool;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuTools;

public class BankCommandTools extends SimpleSubCommand {

    public BankCommandTools(final SimpleCommandGroup parent) {
        super(parent, "tools");

        setDescription("Use this command to give tools to edit bank");
        setPermission("mycteriaeconomy.bank.menu");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        final Menu menu = MenuTools.of(BankTool.class,
                "Use these goodies to fancy",
                "up your bank/branch with ease!");

        menu.displayTo(getPlayer());
    }


}
