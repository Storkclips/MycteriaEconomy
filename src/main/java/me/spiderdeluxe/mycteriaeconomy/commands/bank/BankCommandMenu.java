package me.spiderdeluxe.mycteriaeconomy.commands.bank;

import me.spiderdeluxe.mycteriaeconomy.cache.DataStorage;
import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.menu.bank.BankMenu;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class BankCommandMenu extends SimpleSubCommand {

    public BankCommandMenu(final SimpleCommandGroup parent) {
        super(parent, "menu");

        setDescription("Use this command to display bank menu");
        setPermission("mycteriaeconomy.bank.menu");
    }

    @Override
    protected void onCommand() {
        checkConsole();

        DataStorage.getInstance().getLocalBank();
        DataStorage.getInstance().getStateBank();

        final EconomyPlayer economyPlayer = EconomyPlayer.from(getPlayer());

        new BankMenu(economyPlayer).displayTo(getPlayer());
    }


}
