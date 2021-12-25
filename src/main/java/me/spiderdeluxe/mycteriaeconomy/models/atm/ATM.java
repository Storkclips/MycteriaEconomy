package me.spiderdeluxe.mycteriaeconomy.models.atm;

import lombok.Getter;
import me.spiderdeluxe.mycteriaeconomy.inventories.ATMHolder;
import me.spiderdeluxe.mycteriaeconomy.models.CurrencyItem;
import me.spiderdeluxe.mycteriaeconomy.models.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import me.spiderdeluxe.mycteriaeconomy.util.GUIUtil;
import me.spiderdeluxe.mycteriaeconomy.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

	public Inventory getWithdrawATMGUI(final EconomyPlayer economyPlayer) {
		final Inventory inventory = Bukkit.createInventory(new ATMHolder(this), 36, "ATM");
		GUIUtil.setFrame(inventory, Material.LIME_STAINED_GLASS_PANE);
		// Filling slot 10, 16, 19 and 25
		final ItemStack filler = GUIUtil.getFiller(Material.LIME_STAINED_GLASS_PANE);
		inventory.setItem(10, filler);
		inventory.setItem(16, filler);
		inventory.setItem(19, filler);
		inventory.setItem(25, filler);
		addEconomyItems(inventory);
		addBalanceItem(inventory, economyPlayer.getBankBalance());
		addGuideItem(inventory);
		addFeeItem(inventory);
		return inventory;
	}

	private void addEconomyItems(final Inventory inventory) {
		inventory.setItem(22, getATMItem(CurrencyItem.oneHundredDollarBill()));
		for (final ItemStack economyItem : CurrencyItem.getEconomyItems()) {
			if (CurrencyItem.getValueFromItem(economyItem) == 100) continue;

			inventory.addItem(getATMItem(economyItem));
		}
	}

	private ItemStack getATMItem(final ItemStack economyItem) {
		final ItemMeta meta = economyItem.getItemMeta();
		meta.setDisplayName(StringUtil.formatColor("&2&l$" +
				StringUtil.roundNumber(CurrencyItem.getValueFromItem(economyItem), 2)));
		meta.setLore(Arrays.asList(StringUtil.formatColor("&eClick here to withdraw.")));
		economyItem.setItemMeta(meta);
		return economyItem;
	}

	private void addBalanceItem(final Inventory inventory, final double balance) {
		final ItemStack balanceItem = GUIUtil.getGUIItem(Material.SUNFLOWER,
				StringUtil.formatColor("&aBalance: &a&l$" + StringUtil.roundNumber(balance, 2)), null);
		inventory.setItem(30, balanceItem);
	}

	private void addGuideItem(final Inventory inventory) {
		final List<String> lore = new ArrayList<>();
		lore.add("&eYou can click a bill or coin from your");
		lore.add("&einventory to deposit it into your balance.");
		final ItemStack guideItem = GUIUtil.getGUIItem(Material.COMPASS, "&2Information", lore);
		inventory.setItem(31, guideItem);
	}

	private void addFeeItem(final Inventory inventory) {
		final double transactionFee = Settings.General.ATM_TRANSACTION_FEE;
		final ItemStack feeItem = GUIUtil.getGUIItem(Material.BLAZE_POWDER, "&eTransaction Fee: ",
				Arrays.asList("&c &l$" + StringUtil.roundNumber(transactionFee, 2)));
		inventory.setItem(32, feeItem);
	}

	// --------------------------------------------------------------------------------------------------------------
	// Utility Methods
	// --------------------------------------------------------------------------------------------------------------


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
