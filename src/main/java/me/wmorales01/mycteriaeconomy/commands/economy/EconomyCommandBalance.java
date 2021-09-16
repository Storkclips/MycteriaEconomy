package me.wmorales01.mycteriaeconomy.commands.economy;

import me.wmorales01.mycteriaeconomy.models.EconomyPlayer;
import me.wmorales01.mycteriaeconomy.util.Messager;
import me.wmorales01.mycteriaeconomy.util.SFXManager;
import me.wmorales01.mycteriaeconomy.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyCommandBalance extends EconomyCommand {

    public EconomyCommandBalance() {
        setName("balance");
        setInfoMessage("Displays your current balance.");
        setPermission("mycteriaeconomy.balance");
        setUsageMessage("/economy balance");
        setArgumentLength(1);
        setUniversalCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player;
        if (args.length == 1) { // Executed /economy balance
            if (!(sender instanceof Player)) {
                Messager.sendErrorMessage(sender, "&cNot available for consoles");
                return;
            }
            player = (Player) sender;
            handleBalanceCommand(player);
            return;
        }
        String balanceSubcommand = args[1].toLowerCase();
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
        return "&3/economy balance &3- &aDisplays your curreny bank balance.\n" +
                "&3/economy balance set <Player> <Amount> &3- &aSets the selected player's bank balance to the specified amount.\n" +
                "&3/economy balance <Add/Remove> <Amount> &3- &aModifies the selected player's bank balance by adding or removing the specified amount.";
    }

    /**
     * Handles the /economy balance command, which will display the passed player's balance.
     *
     * @param player player which balance will be displayed.
     */
    private void handleBalanceCommand(Player player) {
        EconomyPlayer economyPlayer = EconomyPlayer.fromPlayer(player);
        double bankBalance = economyPlayer.getBankBalance();
        Messager.sendSuccessMessage(player, "&eYou have a balance of: &3&l$" + StringUtil.roundNumber(bankBalance, 2));
    }

    /**
     * Handles the /economy balance view <player> command, which will display the passed player's balance to the
     * command executor.
     *
     * @param sender entity that executed the command.
     * @param args   arguments used in the command execution.
     */
    private void handleBalanceViewCommand(CommandSender sender, String[] args) {
        if (!(sender.hasPermission("mycteriaeconomy.balance.view"))) {
            Messager.sendNoPermissionMessage(sender);
            return;
        }
        if (args.length < 3) {
            Messager.sendErrorMessage(sender, "&cUsage: &l/economy balance view <Player>");
            return;
        }
        String targetName = args[2];
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            Messager.sendPlayerNotFoundMessage(sender, targetName);
            return;
        }
        double targetBalance = EconomyPlayer.fromPlayer(target).getBankBalance();
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
    private void handleCommandBalanceSet(CommandSender sender, String[] args) {
        if (!sender.hasPermission("mycteriaeconomy.balance.set")) {
            Messager.sendNoPermissionMessage(sender);
            return;
        }
        if (args.length < 4) {
            Messager.sendErrorMessage(sender, "&cUsage: &l/economy balance set <Player> <Amount>");
            return;
        }
        String targetName = args[2];
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            Messager.sendPlayerNotFoundMessage(sender, targetName);
            return;
        }
        String amountString = args[3];
        Double newBalance = StringUtil.parseDouble(sender, amountString);
        if (newBalance == null) return;
        if (newBalance < 0) {
            Messager.sendErrorMessage(sender, "&cThe balance of a player can't be lower than 0.");
            return;
        }
        EconomyPlayer targetEconomyPlayer = EconomyPlayer.fromPlayer(target);
        targetEconomyPlayer.setBankBalance(newBalance);
        String roundedBankBalance = StringUtil.roundNumber(newBalance, 2);
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
    private void handleCommandBalanceChange(CommandSender sender, String[] args) {
        if (!sender.hasPermission("mycteriaeconomy.balance.change")) {
            Messager.sendNoPermissionMessage(sender);
            return;
        }
        if (args.length < 4) {
            Messager.sendErrorMessage(sender, "&cUsage: &l/economy balance <Add/Remove> <Player> <Amount>");
            return;
        }
        String targetName = args[2];
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            Messager.sendPlayerNotFoundMessage(sender, targetName);
            return;
        }
        String amountString = args[3];
        Double amount = StringUtil.parseDouble(sender, amountString);
        if (amount == null) return;
        if (amount <= 0) {
            Messager.sendErrorMessage(sender, "&cThe changed amount must be higher than 0.");
            return;
        }
        EconomyPlayer targetEconomyPlayer = EconomyPlayer.fromPlayer(target);
        // Whether it is /economy balance add or /economy balance remove
        boolean isAddingBalance = args[1].equalsIgnoreCase("add");
        if (isAddingBalance) {
            targetEconomyPlayer.increaseBankBalance(amount);
        } else {
            targetEconomyPlayer.decreaseBankBalance(amount);
        }
        String operation = isAddingBalance ? "added" : "removed";
        String roundedAmount = StringUtil.roundNumber(amount, 2);
        Messager.sendMessage(target, "&3&l$" + roundedAmount + " &ehave been " + operation + " to your bank balance.");
        SFXManager.playPlayerSound(target, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.7F, 1.3F);
        if (sender.equals(target)) return;

        Messager.sendSuccessMessage(sender, "&3&l$" + roundedAmount + " &ahave been successfully " + operation + " to " +
                "&a&l" + targetName + "'s &abank balance.");
    }
}
