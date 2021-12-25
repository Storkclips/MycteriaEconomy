package me.spiderdeluxe.mycteriaeconomy.models.atm;

import me.spiderdeluxe.mycteriaeconomy.inventories.ATMHolder;
import me.spiderdeluxe.mycteriaeconomy.models.CurrencyItem;
import me.spiderdeluxe.mycteriaeconomy.models.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import me.spiderdeluxe.mycteriaeconomy.util.SFXManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ATMListener implements Listener {



    // Listens when a player clicks an inventory, if it is an ATM GUI then handle it according to the executed
    // operation
    @EventHandler
    public void onATMGUIClick(final InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!(event.getInventory().getHolder() instanceof ATMHolder)) return;

        event.setCancelled(true);
        final Player player = (Player) event.getWhoClicked();
        final ItemStack clickedItem = event.getCurrentItem();
        final EconomyPlayer economyPlayer = EconomyPlayer.fromPlayer(player);
        final Inventory clickedInventory = event.getClickedInventory();
        final ATM atm = ((ATMHolder) event.getInventory().getHolder()).getAtm();
        if (clickedItem == null) return;
        if (!CurrencyItem.isCurrencyItem(clickedItem)) return;
        if (clickedInventory.getType() == InventoryType.CHEST) { // Is withdrawing money
            withdrawBankBalance(atm, economyPlayer, clickedItem);
        } else {
            depositBankBalance(atm, economyPlayer, clickedItem);
        }
    }

    // Withdraws the value of the passed economyItem from the passed economyPlayer's bank balance (if possible)
    // and reopens the passed atm's GUI
    private void withdrawBankBalance(final ATM atm, final EconomyPlayer economyPlayer, final ItemStack economyItem) {
        final Player player = economyPlayer.getPlayer();
        final double withdrawValue = CurrencyItem.getValueFromItem(economyItem);
        if (economyPlayer.getBankBalance() - withdrawValue <= 0) {
            Messager.sendErrorMessage(player, "&cYou don't have enough bank balance to execute this transaction.");
            return;
        }
        economyPlayer.decreaseBankBalance(withdrawValue);
        final ItemStack withdrawnItem = CurrencyItem.getItemFromValue(withdrawValue);
        if (withdrawnItem == null) return;
        final Location playerLocation = player.getLocation();
        if (player.getInventory().firstEmpty() == -1) {
            playerLocation.getWorld().dropItemNaturally(playerLocation, withdrawnItem);
        } else {
            player.getInventory().addItem(withdrawnItem);
        }
        SFXManager.playWorldSound(playerLocation, Sound.BLOCK_NOTE_BLOCK_BIT, 0.7F, 1.3F);
        player.openInventory(atm.getWithdrawATMGUI(economyPlayer));
    }

    // Deposit the passed balanceItem's value to the passed economyPlayer's bank account and reopens the
    // passed atm's GUI after deleting the clicked economyItem
    private void depositBankBalance(final ATM atm, final EconomyPlayer economyPlayer, final ItemStack economyItem) {
        final double depositValue = CurrencyItem.getValueFromItem(economyItem);
        economyPlayer.increaseBankBalance(depositValue * economyItem.getAmount());
        economyItem.setAmount(0);

        final Player player = economyPlayer.getPlayer();
        player.openInventory(atm.getWithdrawATMGUI(economyPlayer));
        SFXManager.playWorldSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.7F, 1.3F);
    }


}
