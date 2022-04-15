package me.spiderdeluxe.mycteriaeconomy.conversation;

import me.spiderdeluxe.mycteriaeconomy.models.Wallet;
import me.spiderdeluxe.mycteriaeconomy.models.account.BaseAccount;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import me.spiderdeluxe.mycteriaeconomy.util.BalanceUtil;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.conversation.SimpleConversation;
import org.mineacademy.fo.conversation.SimplePrompt;

public class ATMTransactionAmountPrompt extends SimplePrompt {

    BaseAccount promptAccount;
    boolean isDeposit;

    public ATMTransactionAmountPrompt(final BaseAccount baseAccount, final boolean isDeposit) {
        super(false);
        promptAccount = baseAccount;
        this.isDeposit = isDeposit;
    }

    @Override
    protected String getPrompt(final ConversationContext ctx) {
        return "&6Enter the amount of money you want to " + (isDeposit ? "deposit" : "withdraw");
    }

    @Override
    protected boolean isInputValid(final ConversationContext context, final String input) {
        if (!Valid.isInteger(input))
            return false;


        try {
            final int transactionQuantity = Integer.parseInt(input);

        //    if (transactionQuantity > 200) return false;

            if (isDeposit) {

                final Player player = getPlayer(context);

                // Executing transaction
                final Inventory inventory = player.getInventory();
                final ItemStack walletItem = Wallet.findItemWalletInInv(inventory);
                final Wallet wallet = Wallet.fromItemStack(walletItem);

                final double availableCash;

                if (wallet == null
                        || wallet.getBalance() <= 0) {
                    availableCash = BalanceUtil.computeInventoryBalance(player.getInventory());
                } else {
                    availableCash = wallet.getBalance();
                }

                // Checking if the player has enough cash
                return transactionQuantity <= availableCash;

            } else
                return transactionQuantity <= promptAccount.getBalance();
        } catch (final NumberFormatException formatException) {
            return false;
        }
    }

    @Override
    protected String getFailedValidationText(final ConversationContext context, final String invalidInput) {
        // if (Valid.isInteger(invalidInput) && Integer.parseInt(invalidInput) > 200)
        //      return "You cannot " + (isDeposit ? "deposit" : "withdraw") + " more than $200 at a time";

        if (isDeposit)
            return "You don't have enough money in your inventory, so you can't deposit them";
        else
            return Settings.Messages.INSUFFICIENT_BALANCE.replace("{balance}", String.valueOf(promptAccount.getBalance()));
    }

    @Override
    protected @Nullable
    Prompt acceptValidatedInput(@NotNull final ConversationContext context, @NotNull final String input) {
        final int transactionQuantity = Integer.parseInt(input);
        tell(context, "&6Now you are going to " + (isDeposit ? "deposit" : "withdraw") + " " + transactionQuantity);

        if (isDeposit) {
            final Player player = getPlayer(context);

            // Executing transaction
            final Inventory inventory = player.getInventory();
            final ItemStack walletItem = Wallet.findItemWalletInInv(inventory);
            final int walletSlot = Wallet.findSlotWalletInInv(inventory);
            final Wallet wallet = Wallet.fromItemStack(walletItem);

            // Executing Transaction
            if (wallet == null
                    || wallet.getBalance() < 1) {
                BalanceUtil.removeBalance(player.getInventory(), transactionQuantity);
            } else {
                wallet.decreaseBalance(transactionQuantity);
                inventory.setItem(walletSlot, wallet.getItemStack());
            }
            promptAccount.deposit(transactionQuantity);
        } else {
            BalanceUtil.giveBalance(getPlayer(context), transactionQuantity);
            promptAccount.withdraw(transactionQuantity);
        }

        return Prompt.END_OF_CONVERSATION;
    }

    @Override
    public void onConversationEnd(final SimpleConversation conversation, final ConversationAbandonedEvent event) {
        final Player player = getPlayer(event.getContext());
        tell(player, "Your have successfully " + (isDeposit ? "deposit" : "withdraw") + " your money in the bank!");
        //       new ATMMenu.WalletMenu(new ATMMenu(EconomyPlayer.from(player)), promptAccount, isDeposit).displayTo(player);
    }
}
