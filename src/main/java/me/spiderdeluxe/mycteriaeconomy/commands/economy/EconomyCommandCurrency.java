package me.spiderdeluxe.mycteriaeconomy.commands.economy;

import me.spiderdeluxe.mycteriaeconomy.inventories.CurrencyGUI;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

/**
 * @author  SpiderDeluxe
 * This command is used to open a GUI with all the available currency items.
 */
public class EconomyCommandCurrency extends SimpleSubCommand {

    public EconomyCommandCurrency(final SimpleCommandGroup parent) {
        super(parent, "currency");

        setDescription("Opens a GUI with all the available currency items.");
        setPermission("mycteriaeconomy.currency");
    }

    @Override
    public void onCommand() {
        checkConsole();

        final Player player = (Player) sender;
        player.openInventory(new CurrencyGUI().getGUI());
    }
}
