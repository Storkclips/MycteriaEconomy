package me.spiderdeluxe.mycteriaeconomy.models.bank;

import lombok.Getter;
import me.spiderdeluxe.mycteriaeconomy.models.Wallet;
import me.spiderdeluxe.mycteriaeconomy.models.account.BaseAccount;
import me.spiderdeluxe.mycteriaeconomy.util.BalanceUtil;
import me.spiderdeluxe.mycteriaeconomy.util.CurrencyItem;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import me.spiderdeluxe.mycteriaeconomy.util.SFXManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author SpiderDeluxe
 * This class deals with the management of ATM
 */
public class ATM {
	/**
	 * Stores active atm by their UUID, atm are singletons
	 */
	@Getter
	private static final Map<UUID, ATM> byUUID = new HashMap<>();

	/**
	 * The uuid of this ATM
	 */
	@Getter
	private UUID uuid;


	public ATM(final UUID uuid) {
		this.uuid = uuid;
	}

	// --------------------------------------------------------------------------------------------------------------
	// ATM manipulation static access
	// --------------------------------------------------------------------------------------------------------------
	public static ATM createATM(final UUID uuid) {

		final ATM atm = new ATM(uuid);

		byUUID.put(uuid, atm);
		return atm;
	}

	public void deleteATM() {
		final ATM atm = this;

		byUUID.remove(atm.getUuid(), atm);
	}


	// --------------------------------------------------------------------------------------------------------------
	// ATM item static access
	// --------------------------------------------------------------------------------------------------------------

	// --------------------------------------------------------------------------------------------------------------
	// Utility Methods
	// --------------------------------------------------------------------------------------------------------------

	/**
	 * Withdraws the value of the passed economyItem from the passed economyPlayer's bank balance (if possible)
	 * and reopens the passed atm's GUI
	 */
	public static void withdrawAccountMoney(final BaseAccount account, final ItemStack economyItem) {
		final Player player = account.getOwner();

		if (player == null) return;

		final int withdrawValue = (int) CurrencyItem.getValueFromItem(economyItem);
		if ((account.getBalance() - withdrawValue) < 0) {
			Messager.sendErrorMessage(player, "&cYou don't have enough bank balance to execute this transaction.");
			return;
		}

		account.withdraw(withdrawValue);

		final ItemStack withdrawnItem = CurrencyItem.getItemFromValue(withdrawValue);
		if (withdrawnItem == null) return;
		final Location playerLocation = player.getLocation();
		if (player.getInventory().firstEmpty() == -1) {
			playerLocation.getWorld().dropItemNaturally(playerLocation, withdrawnItem);
		} else {
			player.getInventory().addItem(withdrawnItem);
		}
		SFXManager.playWorldSound(playerLocation, Sound.BLOCK_NOTE_BLOCK_BIT, 0.7F, 1.3F);
	}


	/**
	 * Deposit the passed balanceItem's value to the passed economyPlayer's bank account and reopens the
	 * passed atm's GUI after deleting the clicked economyItem
	 */
	public static void depositAccountMoney(final BaseAccount account, final ItemStack economyItem) {
		final double depositValue = CurrencyItem.getValueFromItem(economyItem);

		final Player player = account.getOwner();

		// Executing transaction
		final Inventory inventory = player.getInventory();
		final ItemStack walletItem = Wallet.findItemWalletInInv(inventory);
		final int walletSlot = Wallet.findSlotWalletInInv(inventory);
		final Wallet wallet = Wallet.fromItemStack(walletItem);

		final double availableCash;

		if (wallet == null
				|| wallet.getBalance() <= 0) {
			availableCash = BalanceUtil.computeInventoryBalance(player.getInventory());
		} else {
			availableCash = wallet.getBalance();
		}

		// Checking if the player has enough cash
		if (availableCash < depositValue) {
			Messager.sendErrorMessage(player, "&cYou don't have enough funds to execute this transaction.");
			return;
		}

		// Executing Transaction
		if (wallet == null
				|| wallet.getBalance() < 1) {
			BalanceUtil.removeBalance(player.getInventory(), depositValue);
			player.updateInventory();
		} else {
			wallet.decreaseBalance(depositValue);
			inventory.setItem(walletSlot, wallet.getItemStack());
		}
		account.deposit((int) (depositValue));
		SFXManager.playWorldSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.7F, 1.3F);
	}

	/**
	 * Check if already exist an Atm with this uuid
	 *
	 * @param uuid the uuid of atm
	 */
	public static boolean alreadyExist(final UUID uuid) {
		return findATM(uuid) != null;
	}
	// --------------------------------------------------------------------------------------------------------------
	// Static access
	// --------------------------------------------------------------------------------------------------------------


	/**
	 * Get the ATM by its uuid
	 *
	 * @param uuid the uuid of atm
	 */
	public static ATM findATM(final UUID uuid) {
		for (final ATM atm : getATMS())
			if (atm.getUuid().equals(uuid))
				return atm;

		return null;
	}

	/**
	 * Return all atms
	 *
	 * @return
	 */
	public static Collection<ATM> getATMS() {
		return Collections.unmodifiableCollection(byUUID.values());
	}

}
