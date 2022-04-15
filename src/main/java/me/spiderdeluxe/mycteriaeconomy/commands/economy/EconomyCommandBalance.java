package me.spiderdeluxe.mycteriaeconomy.commands.economy;

import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.models.account.BaseAccount;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import me.spiderdeluxe.mycteriaeconomy.util.SFXManager;
import me.spiderdeluxe.mycteriaeconomy.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * @author  SpiderDeluxe
 * This command is used to display your current balance.
 */
public class EconomyCommandBalance extends SimpleSubCommand {

    public EconomyCommandBalance(final SimpleCommandGroup parent) {
        super(parent, "balance");

        setDescription("Displays your current balance.");
        setPermission("mycteriaeconomy.balance");
        setUsage("<player>");
        setMinArguments(1);
    }

    @Override
    public void onCommand() {
        checkConsole();

        final String balanceSubcommand = args[0].toLowerCase();

        if (balanceSubcommand.equals("help")) {
            Messager.sendSuccessMessage(sender, getBalanceHelpMessage());
            return;
        }
        if (balanceSubcommand.equals("view")) { // Executed /economy balance view
            handleBalanceViewCommand(sender, args);
            return;
        }
        if (balanceSubcommand.equals("set")) {
            handleCommandBalanceSet(sender, args);
            return;
        }
        if (balanceSubcommand.equals("add") || balanceSubcommand.equals("remove")) {
            handleCommandBalanceChange(sender, args);
            return;
        }
        Messager.sendSuccessMessage(sender, getBalanceHelpMessage());
    }

    /**
     * Returns a String with all the /economy balance commands and their respective information
     *
     * @return A String with all the commands and their information
     */
    private String getBalanceHelpMessage() {
        return "&3/economy balance <account> &3- &aDisplays your curreny bank balance.\n" +
                "&3/economy balance set <Player> <account> <Amount> &3- &aSets the selected player's bank balance to the specified amount.\n" +
                "&3/economy balance <Add/Remove> <account> <Amount> &3- &aModifies the selected player's bank balance by adding or removing the specified amount.";
    }


    /**
     * Handles the /economy balance view <player> command, which will display the passed player's balance to the
     * command executor.
     *
     * @param sender entity that executed the command.
     * @param args   arguments used in the command execution.
     */
    private void handleBalanceViewCommand(final CommandSender sender, final String[] args) {
        if (!(sender.hasPermission("mycteriaeconomy.balance.view"))) {
            Messager.sendNoPermissionMessage(sender);
            return;
        }
        if (args.length < 3) {
            Messager.sendErrorMessage(sender, "&cUsage: &l/economy balance view <player> <account>");
            return;
        }
        final String targetName = args[1];
        final Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            Messager.sendPlayerNotFoundMessage(sender, targetName);
            return;
        }

        final EconomyPlayer ecoPlayer = EconomyPlayer.from(target);
        checkBoolean(ecoPlayer.getAccounts() != null, "does not currently have an active bank account.");

        checkBoolean(Valid.isInteger(args[2]), "You must write down a number to identify your bank account.");
        final BaseAccount count = BaseAccount.findByAccount(Integer.parseInt(args[2]));
        checkBoolean(count != null, "does not currently have an active bank account with id: " + args[2]);

        assert count != null;

        final double targetBalance = count.getBalance();

        Messager.sendSuccessMessage(sender, "&e&l" + targetName + " &ahas a balance of &3&l$" +
                StringUtil.roundNumber(targetBalance, 2));
    }

    /**
     * Handles the /economy balance set <Player> <Amount> command, which will set the passed player's balance to the specified
     * amount.
     *
     * @param sender entity that is executing the command.
     * @param args   arguments used when the command was executed.
     */
    private void handleCommandBalanceSet(final CommandSender sender, final String[] args) {
        if (!sender.hasPermission("mycteriaeconomy.balance.set")) {
            Messager.sendNoPermissionMessage(sender);
            return;
        }
        if (args.length < 4) {
            Messager.sendErrorMessage(sender, "&cUsage: &l/economy balance set <Player> <Count> <Amount>");
            return;
        }
        final String targetName = args[1];
        final Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            Messager.sendPlayerNotFoundMessage(sender, targetName);
            return;
        }

        final EconomyPlayer ecoPlayer = EconomyPlayer.from(target);

        checkBoolean(ecoPlayer.getAccounts() != null, "does not currently have an active bank account.");


        checkBoolean(Valid.isInteger(args[2]), "You must write down a number to identify your bank account.");
        final BaseAccount count = BaseAccount.findByAccount(Integer.parseInt(args[2]));
        checkBoolean(count != null, "does not currently have an active bank account with id: " + args[2]);

        final int amount = Integer.parseInt(args[3]);

        assert count != null;
        count.setBalance(amount);

        final String roundedBankBalance = StringUtil.roundNumber(amount, 2);
        Messager.sendMessage(target, "&eYour bank balance has been set to &3&l$" + roundedBankBalance + "&e.");
        SFXManager.playPlayerSound(target, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.7F, 1.3F);
        if (sender.equals(target)) return;

        Messager.sendSuccessMessage(sender, "&e&l" + targetName + "'s &ebank balance has been successfully set to &3&l$" +
                roundedBankBalance + "&e.");
    }

    /**
     * Handles the commands /economy balance add <Player> <Amount> and /economy balance remove <Player> <Amount>.
     *
     * @param sender Entity that executed the command.
     * @param args   Arguments used when the command was executed.
     */
    private void handleCommandBalanceChange(final CommandSender sender, final String[] args) {
        if (!sender.hasPermission("mycteriaeconomy.balance.change")) {
            Messager.sendNoPermissionMessage(sender);
            return;
        }
        if (args.length < 4) {
            Messager.sendErrorMessage(sender, "&cUsage: &l/economy balance <Add/Remove> <Player> <Amount>");
            return;
        }
        final String targetName = args[1];
        final Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            Messager.sendPlayerNotFoundMessage(sender, targetName);
            return;
        }
        final EconomyPlayer ecoPlayer = EconomyPlayer.from(target);
        checkBoolean(ecoPlayer.getAccounts() != null, "does not currently have an active bank account.");


        checkBoolean(Valid.isInteger(args[2]), "You must write down a number to identify your bank account.");
        final BaseAccount count = BaseAccount.findByAccount(Integer.parseInt(args[2]));
        checkBoolean(count != null, "does not currently have an active bank account with id: " + args[2]);

        final int amount = Integer.parseInt(args[3]);

        assert count != null;

        // Whether it is /economy balance add or /economy balance remove
        final boolean isAddingBalance = args[0].equalsIgnoreCase("add");
        if (isAddingBalance) {
            count.increaseBalance(amount);
        } else {
            count.decreaseBalance(amount);
        }
        final String operation = isAddingBalance ? "added" : "removed";
        final String roundedAmount = StringUtil.roundNumber(amount, 2);
        Messager.sendMessage(target, "&3&l$" + roundedAmount + " &ehave been " + operation + " to your bank balance.");
        SFXManager.playPlayerSound(target, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.7F, 1.3F);
        if (sender.equals(target)) return;

        Messager.sendSuccessMessage(sender, "&3&l$" + roundedAmount + " &ahave been successfully " + operation + " to " +
                "&a&l" + targetName + "'s &abank balance.");
    }

    @Override
    protected List<String> tabComplete() {

        switch (args.length) {
            case 1 -> {
                return completeLastWord("help", "view", "set", "add", "remove");
            }
            case 2 -> {
                return completeLastWordPlayerNames();
            }
            case 3 -> {
                final String param = args[0];

                if ("set".equals(param)
                        || "add".equals(param)
                        || "remove".equals(param)
                        || "view".equals(param)) {

                    final Player player = Bukkit.getPlayer(args[1]);

                    if (player == null) return new ArrayList<>();

                    final EconomyPlayer economyPlayer = EconomyPlayer.from(player);

                    return completeLastWord(economyPlayer.getCountsNumbers());
                }
            }
        }
        return new ArrayList<>();
    }

}
