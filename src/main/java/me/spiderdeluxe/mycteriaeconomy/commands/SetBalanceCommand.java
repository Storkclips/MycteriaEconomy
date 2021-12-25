package me.spiderdeluxe.mycteriaeconomy.commands;

import me.spiderdeluxe.mycteriaeconomy.models.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import me.spiderdeluxe.mycteriaeconomy.util.Parser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommand;

/**
 * @author wmorale01, SpiderDeluxe
 * This command is used to display your current balance.
 */
public class SetBalanceCommand extends SimpleCommand {

    public SetBalanceCommand() {
        super( "setbalance");
        setPermission("economyplugin.setbalance");
        setMinArguments(2);
        setUsage("<player> <amount>");
    }

    @Override
    public void onCommand() {
        checkConsole();

        final Player receiver = Bukkit.getPlayer(args[0]);
        if (receiver == null) {
            Messager.sendMessage(sender, "&cPlayer not found.");
            return;
        }
        final EconomyPlayer ecoPlayer = EconomyPlayer.fromPlayer(receiver);
        final Integer amount = Parser.getNumber(sender, args[1]);
        ecoPlayer.setBankBalance(amount);

        Messager.sendMessage(sender, "&a&l" + receiver.getDisplayName() + " &areceived &l" + amount + "$&a.");
    }
}
