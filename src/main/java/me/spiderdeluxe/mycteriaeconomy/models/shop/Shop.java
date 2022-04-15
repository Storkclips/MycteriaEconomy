package me.spiderdeluxe.mycteriaeconomy.models.shop;

import lombok.Getter;
import lombok.Setter;
import me.spiderdeluxe.mycteriaeconomy.cache.DataStorage;
import me.spiderdeluxe.mycteriaeconomy.models.Wallet;
import me.spiderdeluxe.mycteriaeconomy.models.machine.LinkedChest;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import me.spiderdeluxe.mycteriaeconomy.util.BalanceUtil;
import me.spiderdeluxe.mycteriaeconomy.util.InventoryUtil;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import me.spiderdeluxe.mycteriaeconomy.util.SFXManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.*;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.settings.YamlConfig;

import java.io.File;
import java.util.*;

@Getter
public class Shop extends YamlConfig {


	/**
	 * Stores active merchants by their name, so they are singletons
	 */
	private static final Map<String, Shop> byName = new HashMap<>();


	/**
	 * Stores all linkedChest by their Location, chest are singletons
	 */
	@Getter
	@Setter
	private static Map<Shop, Chest> allLinkedChests = new HashMap<>();


	/**
	 * This is the date when the shop was created
	 */
	@Getter
	@Setter
	private String creationDate;

	/**
	 * This is the displayName, that is the name that appears in the menus and in the messages,
	 * it differs from the shop identification name that is the one of the file
	 */
	@Getter
	@Setter
	private String displayName;

	/**
	 * This field contains the type of the shop.
	 * It could be VENDING or SELLING
	 */
	@Getter
	@Setter
	private ShopType type;

	/**
	 * This field contains the type of the shop owner
	 * It could be STATE or PLAYER
	 */
	@Getter
	@Setter
	private ShopOwnerType owner;

	/**
	 * This field contains the owner player of this shop
	 */
	@Getter
	@Setter
	private OfflinePlayer ownerPlayer;

	/**
	 * This field contains the profit of this shop
	 */
	@Getter
	@Setter
	private Double profit;

	/**
	 * This field contains all chest linked to this shop
	 */
	@Getter
	@Setter
	private Set<Chest> linkedChests = new HashSet<>();

	/**
	 * This field contains all chest linked to this shop
	 */
	@Getter
	@Setter
	private Set<LinkedChest> activeLinkedChests = new HashSet<>();


	/**
	 * Stores active items by their uuid, so they are singletons
	 */
	private final Set<ItemShop> items = new HashSet<>();

	public Shop(final String shop) {
		setHeader("Welcome to the main class settings file!");

		loadConfiguration("shops.yml", "shops/" + shop + (!shop.endsWith(".yml") ? ".yml" : ""));
	}


	@Override
	protected void onLoadFinish() {
		items.clear();

		creationDate = getString("Creation_Date");
		displayName = getString("Display_Name");


		type = ShopType.fromName(getString("Shop_Type"));
		owner = ShopOwnerType.fromName(getString("Owner_Type"));

		if (owner == ShopOwnerType.PLAYER && isSet("Owner_Player"))
			ownerPlayer = Bukkit.getOfflinePlayer(UUID.fromString(getString("Owner_Player")));

		profit = getDouble("Profit");

		if (isSet("Items")) {
			// It is basically stored as a list however each list item is a hashmap
			// containing keys and values
			final List<SerializedMap> itemList = getMapList("Items");

			if (!itemList.isEmpty()) {
				for (final SerializedMap itemsSettings : itemList) {

					final UUID uuid = itemsSettings.getUUID("UUID");
					final String itemName = itemsSettings.getString("Name");
					Valid.checkNotNull(itemName, "Malformed items settings, lacking the Name key: " + itemsSettings);

					final int quantity = itemsSettings.getInteger("Quantity", 1);
					final double price = itemsSettings.getDouble("Price", 1D);
					final int stock = itemsSettings.getInteger("Stock", 10);

					if (itemsSettings.containsKey("Item_Info")) {
						final ItemStack itemStack = itemsSettings.getItem("Item_Info");

						items.add(new ItemShop(uuid, itemName, quantity, price, stock, itemStack));
					}
				}

				if (isSet("Linked_Chests")) {
					final List<SerializedMap> chestList = getMapList("Linked_Chests");
					for (final SerializedMap chestSettings : chestList) {

						final Location location = chestSettings.getLocation("Location");
						final Block block = location.getWorld().getBlockAt(location);
						if (block.getType() == Material.CHEST
								|| block.getType() == Material.TRAPPED_CHEST) {
							final Chest chest = (Chest) block.getState();
							if (!linkedChests.contains(chest)) {
								linkedChests.add(chest);
								activeLinkedChests.add(new LinkedChest(location.getWorld().getBlockAt(location)));
							}
						}
					}

				}
			}
		}

	}

	public static void loadAll() {
		byName.clear();

		for (final File file : FileUtil.getFiles("shops", "yml")) {
			final Shop shop = new Shop(file.getName().replace(".yml", "")); // You can also remove the extension here directly

			byName.put(shop.getName(), shop);
		}
		Common.log("Loaded Shop: " + byName.size());
	}


	// --------------------------------------------------------------------------------------------------------------
	// Shop manipulation
	// --------------------------------------------------------------------------------------------------------------

	public static Shop createShop(final OfflinePlayer player, final String name) {
		Valid.checkBoolean(!alreadyExist(name),
				"Already exist a shop with this name");

		final Shop shop = new Shop(name);
		shop.save();

		shop.setMachineInfo(player, name);

		byName.put(shop.getName(), shop);
		return shop;
	}

	public static Shop createShop(final String name) {
		Valid.checkBoolean(!alreadyExist(name),
				"Already exist a shop with this name");

		final Shop shop = new Shop(name);
		shop.save();

		byName.put(shop.getName(), shop);
		return shop;
	}


	public void deleteShop() {
		Valid.checkNotNull(this, "This shop doesn't exist for now!");

		delete();
		byName.remove(this.getName(), this);
	}


	/**
	 * This method is used to change the creation date of shop
	 *
	 * @param time new creationData
	 */
	public void changeCreationDate(final String time) {
		Valid.checkNotNull(this, "This shop doesn't exist for now!");

		creationDate = time;
		save("Creation_Date", creationDate);
	}


	/**
	 * This method is used to change the display name of shop
	 *
	 * @param name new displayName
	 */
	public void changeDisplayName(final String name) {
		Valid.checkNotNull(this, "This shop doesn't exist for now!");

		displayName = name;
		save("Display_Name", displayName);
	}


	/**
	 * This method is used to set the type of shop
	 *
	 * @param type
	 */
	public void changeType(final ShopType type) {
		Valid.checkNotNull(this, "This shop doesn't exist for now!");
		this.setType(type);
		this.save("Shop_Type", type);

	}

	/**
	 * This method is used to set the type of shop
	 *
	 * @param type
	 */
	public void changeOwner(final ShopOwnerType type, final OfflinePlayer player) {
		Valid.checkNotNull(this, "This shop doesn't exist for now!");

		setOwner(type);
		save("Owner_Type", type);
		if (player != null) {
			setOwnerPlayer(player);
			save("Owner_Player", player.getUniqueId());
		}

	}

	// --------------------------------------------------------------------------------------------------------------
	// Admin Shop related method
	// --------------------------------------------------------------------------------------------------------------


	// --------------------------------------------------------------------------------------------------------------
	// Player Machines related method
	// --------------------------------------------------------------------------------------------------------------

	public void setMachineInfo(final OfflinePlayer player, final String name) {
		changeCreationDate(TimeUtil.getFormattedDateShort());
		changeDisplayName(name);
		changeType(ShopType.TRADING);
		changeOwner(ShopOwnerType.PLAYER, player);
	}

	/**
	 * Gives the collected balance of the passed Shop to the passed Player in currency items.
	 *
	 * @param player Player that will receive the Shop Balance.
	 */
	public void collectShopProfit(final Player player) {
		final double shopBalance = getProfit();
		setProfit(0D);

		if (shopBalance == 0) {
			Messager.sendErrorMessage(player, "&cThere's no balance to withdraw.");
			return;
		}
		// Convert the balance to currency
		final List<ItemStack> currencyItems = BalanceUtil.balanceToCurrency(shopBalance);
		InventoryUtil.giveItems(player, currencyItems);
		SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 0.6F, 1.3F);
	}
	// --------------------------------------------------------------------------------------------------------------
	// Chest Manipulation
	// --------------------------------------------------------------------------------------------------------------

	public void linkChest(final Chest chest) {
		final Set<Chest> nearestChests = findNearestChest(chest);
		nearestChests.removeAll(linkedChests);

		linkedChests.addAll(nearestChests);
		final Set<LinkedChest> linkedChests = new HashSet<>();
		for (final Chest nearestChest : nearestChests) {
				linkedChests.add(new LinkedChest(nearestChest.getBlock()));
		}
		activeLinkedChests.addAll(linkedChests);
		save("Linked_Chests", activeLinkedChests);
	}

	public Set<Chest> findNearestChest(final Chest originChest) {
		final Set<Chest> nearestChest = new HashSet<>();

		nearestChest.add(originChest);
		for (final Block block : BlockUtil.getBlocks(originChest.getLocation(),
				Settings.General.LINK_MACHINE_HEIGHT,
				Settings.General.LINK_MACHINE_RADIUS + 1)) {
			if (block.getState() instanceof Chest &&
					(block.getType() == Material.CHEST
							|| block.getType() == Material.TRAPPED_CHEST)) {
				final Chest chest = (Chest) block.getState();
				nearestChest.add(chest);
			}
		}
		return nearestChest;
	}


	public void unlinkChest(final Chest chest) {
		linkedChests.remove(chest);
		activeLinkedChests.remove(new LinkedChest(chest.getBlock()));
		save("Linked_Chests", activeLinkedChests);
	}

	// --------------------------------------------------------------------------------------------------------------
	// Shop Profit Manipulation
	// --------------------------------------------------------------------------------------------------------------


	public void increaseProfit(final double amount) {
		Valid.checkNotNull(this, "This shop doesn't exist for now!");

		if (owner == ShopOwnerType.STATE)
			DataStorage.getInstance().increaseFounds(amount);
		else {
			profit += amount;
			save("Profit", profit);
		}
	}

	public void decreaseProfit(final double amount) {
		Valid.checkNotNull(this, "This shop doesn't exist for now!");

		if (owner == ShopOwnerType.STATE)
			DataStorage.getInstance().decreaseFounds(amount);
		else {
			profit -= amount;
			save("Profit", profit);
		}
	}


	// --------------------------------------------------------------------------------------------------------------
	// ItemShop Manipulation
	// --------------------------------------------------------------------------------------------------------------


	/**
	 * This method is used to add a new item to a shop
	 *
	 * @param itemStack the itemStack of the new item
	 */
	public ItemShop addItem(final UUID uuid, final ItemStack itemStack) {
		Valid.checkNotNull(this, "This shop doesn't exist for now!");

		final ItemStack newItem = itemStack.clone();
		newItem.setAmount(1);
		final ItemShop itemShop = new ItemShop(uuid, itemStack.getType().name(), 0, 0, 0, newItem);
		items.add(itemShop);
		save("Items", items);
		return itemShop;
	}


	/**
	 * This method is used to remove an item from a shop
	 *
	 * @param itemShop the ItemShop
	 */
	public void removeItem(final ItemShop itemShop) {
		Valid.checkNotNull(this, "This shop doesn't exist for now!");

		if (itemShop != null) {
			items.remove(itemShop);
			save("Items", items);
		}
	}


	/**
	 * This method is used to set the price of item in a shop
	 *
	 * @param newPrice the new price
	 */
	public void editItemPrice(final ItemShop itemShop, final double newPrice) {
		Valid.checkNotNull(this, "This shop doesn't exist for now!");


		if (itemShop != null) {
			itemShop.setPrice(newPrice);
			save("Items", items);
		}
	}


	/**
	 * This method is used to set the quantity of item in a shop
	 *
	 * @param newQuantity the new quantity
	 */
	public void editItemQuantity(final ItemShop itemShop, final int newQuantity) {
		Valid.checkNotNull(this, "This shop doesn't exist for now!");


		if (itemShop != null) {
			itemShop.setQuantity(newQuantity);
			save("Items", items);
		}
	}

	// --------------------------------------------------------------------------------------------------------------
	// ItemShop Stock Manipulation
	// --------------------------------------------------------------------------------------------------------------


	/**
	 * Iterates through all the Shop Items looking into every linked chest and updates the available stock of each
	 * one of them.
	 */
	public void updateShopItemsStock() {
		for (final ItemShop itemShop : getItems()) {
			int shopItemStock = 0;
			for (final LinkedChest linkedChest : activeLinkedChests) {

				final Block block = linkedChest.getChestBlock();
				// Making sure there is still a chest in the location
				if (!(block.getLocation().getBlock().getState() instanceof Chest)) continue;

				final Chest chest = (Chest) block.getState();
				final Inventory chestInventory = chest.getInventory();

				if (isChestLeft(chest)) {
					continue;
				}

				shopItemStock += InventoryUtil.countItem(itemShop.getItemStack().clone(), chestInventory);
			}
			itemShop.setStock(shopItemStock);
		}
		save("Items", items);
	}

	public boolean isChestLeft(final Chest chest) {
		final BlockData data = chest.getBlockData();
		return data.getAsString().contains("type=left");
	}

	/**
	 * Goes through all the chest slots checking for the available inventory slots and compares it to the passed amount.
	 *
	 * @param itemShop      Shop Item that will be checked.
	 * @param requiredSlots Amount of Shop Items to check.
	 * @return True if there is enough inventory slots to store the passed amount.
	 */
	public boolean canReceiveShopItemStock(final ItemShop itemShop, final int requiredSlots) {
		final ItemStack shopItemStack = itemShop.getItemStack().clone();
		int availableSlots = 0;
		for (final Chest chest : linkedChests) {
			final Inventory chestInventory = chest.getInventory();
			availableSlots += InventoryUtil.countAvailableSlots(shopItemStack, chestInventory);

			if (availableSlots >= requiredSlots) return true;
		}
		return false;
	}

	/**
	 * Adds the passed amount of Shop Items to the linked chests.
	 *
	 * @param itemShop ShopItem that will be added to the linked chests.
	 * @param amount   Amount of the MachineItem that will be added.
	 */
	public void increaseShopItemStock(final ItemShop itemShop, final int amount) {
		final ItemStack shopItemStack = itemShop.getItemStack().clone();
		shopItemStack.setAmount(amount);
		for (final Chest chest : linkedChests) {
			final Inventory chestInventory = chest.getInventory();
			if (InventoryUtil.countAvailableSlots(shopItemStack, chestInventory) == 0) continue;

			final Map<Integer, ItemStack> remainingItems = chestInventory.addItem(shopItemStack);
			if (remainingItems.isEmpty()) break;

			final int remainingAmount = remainingItems.values().iterator().next().getAmount();
			shopItemStack.setAmount(remainingAmount);
		}
	}

	/**
	 * Removes the passed amount of shop items from the linked chests.
	 *
	 * @param itemShop MachineItem that will be removed from the linked chests.
	 * @param amount   Amount of the MachineItem that will be removed.
	 */
	public void decreaseShopItemStock(final ItemShop itemShop, final int amount) {
		final ItemStack shopItemStack = itemShop.getItemStack().clone();
		shopItemStack.setAmount(amount);
		for (final Chest linkedChest : linkedChests) {
			final Inventory chestInventory = linkedChest.getInventory();
			if (!chestInventory.containsAtLeast(shopItemStack, 1)) continue;

			final int availableChestStock = InventoryUtil.countItem(shopItemStack, chestInventory);
			chestInventory.removeItem(shopItemStack);
			if (availableChestStock < amount) {
				shopItemStack.setAmount(amount - availableChestStock);
				continue;
			}
			break;
		}
		updateShopItemsStock();
	}

	/**
	 * Create a uuid to identify the bound by verifying that there is no identical one already present
	 *
	 * @return the Bound's uuid
	 */
	public static UUID createItemUUID(final Shop shop) {
		UUID uuid = UUID.randomUUID();
		while (getItemsUUID(shop).contains(uuid)) {
			uuid = UUID.randomUUID();
		}
		return uuid;
	}


	// --------------------------------------------------------------------------------------------------------------
	// Utility Methods
	// --------------------------------------------------------------------------------------------------------------

	public boolean isChestLinked(final Chest chest) {
		return linkedChests.contains(chest);
	}

	/**
	 * Check if already exist a shop with this name
	 *
	 * @param name the name of shop
	 */
	public static boolean alreadyExist(final String name) {
		return findShop(name) != null;
	}

	/**
	 * Check if a player is owner or has admins permissions
	 *
	 * @param shop   the shop
	 * @param player the player
	 */
	public static boolean hasAdminPermissions(final Shop shop, final Player player) {
		if (shop.ownerPlayer == player
				|| player.hasPermission("mycteriaeconomy.playerShop.access")) {
			return true;
		}
		return shop.owner == ShopOwnerType.STATE
				&& player.hasPermission("mycteriaeconomy.stateShop");
	}

	// --------------------------------------------------------------------------------------------------------------
	// Method related to ItemShop
	// --------------------------------------------------------------------------------------------------------------

	/**
	 * Purchases the passed MachineItem from the passed VendingMachine if the machine has enough stock and the
	 * player has enough cash.
	 *
	 * @param player            Player that is executing the transaction.
	 * @param item              Item involved in the transaction.
	 * @param transactionAmount Amount of the transaction.
	 */
	public void buyShopItem(final Player player, final ItemShop item,
							final int transactionAmount) {
		// Checking if the machine has enough stock
		final int machineItemStock = item.getStock();
		if (machineItemStock < transactionAmount) {
			Messager.sendErrorMessage(player, "&cThere's not enough stock to buy this amount of items.");
			return;
		}
		// Calculating price of the transaction
		final double transactionPrice = item.getPrice() * transactionAmount;
		final Inventory inventory = player.getInventory();
		final ItemStack walletItem = Wallet.findItemWalletInInv(inventory);
		final int walletSlot = Wallet.findSlotWalletInInv(inventory);
		final Wallet wallet = Wallet.fromItemStack(walletItem);
		// Getting balance from either the player inventory or placed wallet if available
		final double availableCash;
		if (wallet == null
				|| wallet.getBalance() <= 0) {
			availableCash = BalanceUtil.computeInventoryBalance(player.getInventory());
		} else {
			availableCash = wallet.getBalance();
		}
		// Checking if the player has enough cash
		if (availableCash < transactionPrice) {
			Messager.sendErrorMessage(player, "&cYou don't have enough funds to execute this transaction.");
			return;
		}
		// Executing Transaction
		decreaseShopItemStock(item, transactionAmount);
		final ItemStack purchasedItemStack = item.getItemStack().clone();
		purchasedItemStack.setAmount(transactionAmount);
		if (wallet == null
				|| wallet.getBalance() < 1) {
			BalanceUtil.removeBalance(player.getInventory(), transactionPrice);
		} else {
			wallet.decreaseBalance(transactionPrice);
			inventory.setItem(walletSlot, wallet.getItemStack());
		}
		InventoryUtil.giveItem(player, purchasedItemStack);
		increaseProfit(transactionPrice);

		SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 0.6F, 1.4F);
	}

	/**
	 * Sells the passed Shop Item to the passed machine. The sold shop items will be taken from the passed
	 * player's inventory.
	 *
	 * @param player            Player that is executing the transaction.
	 * @param item              Item involved in the transaction.
	 * @param transactionAmount Amount of the transaction.
	 */
	public void sellShopItem(final Player player, final ItemShop item,
							 final int transactionAmount) {
		// Checking if the machine has enough storage

		if (!canReceiveShopItemStock(item, transactionAmount)) {
			Messager.sendErrorMessage(player, "&cThere's not enough storage in order for this machine to receive " +
					"this amount of items.");
			return;
		}
		// Checking if player has enough of the item that is being sold
		final ItemStack soldItemStack = item.getItemStack().clone();
		if (!player.getInventory().containsAtLeast(soldItemStack, transactionAmount)) {
			Messager.sendErrorMessage(player, "&cYou don't have enough items to sell this amount to the machine.");
			return;
		}
		// Calculating transaction price and checking if the machine has enough balance for it
		final double transactionPrice = item.getPrice() * transactionAmount;

		if (getBalance() < transactionPrice) {
			Messager.sendErrorMessage(player, "&cThere's not enough cash on the machine to purchase this amount of " +
					"items.");
			return;
		}

		// Executing transaction
		increaseShopItemStock(item, transactionAmount);
		final Inventory inventory = player.getInventory();
		final ItemStack walletItem = Wallet.findItemWalletInInv(inventory);
		final int walletSlot = Wallet.findSlotWalletInInv(inventory);
		final Wallet wallet = Wallet.fromItemStack(walletItem);

		decreaseProfit(transactionPrice);
		// Adding cash to the player's inventory or the placed wallet if available
		if (wallet == null) {
			BalanceUtil.giveBalance(player, transactionPrice);
		} else {
			wallet.increaseWalletBalance(transactionPrice);
			inventory.setItem(walletSlot, wallet.getItemStack());
		}
		soldItemStack.setAmount(transactionAmount);
		player.getInventory().removeItem(soldItemStack);

		SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 0.6F, 1.4F);
	}


	// --------------------------------------------------------------------------------------------------------------
	// Static access
	// --------------------------------------------------------------------------------------------------------------

	/**
	 * Get the shop by its name
	 *
	 * @param name
	 * @return
	 */
	public static Shop findShop(final String name) {
		for (final Shop shop : getShops())
			if (shop.getName().equals(name))
				return shop;

		return null;
	}


	/**
	 * Get the shop by a chest
	 *
	 * @param chest the chest
	 */
	public static Shop findByChest(final Chest chest) {
		for (final Shop shop : getShops())
			if (shop.getLinkedChests().contains(chest))
				return shop;

		return null;
	}

	/**
	 * Get the shop by its playerOwner
	 * Use only with machine
	 *
	 * @param player the player
	 */
	public static Shop findByPlayerOwner(final OfflinePlayer player) {
		for (final Shop shop : getShops()) {
			if (shop.getOwnerPlayer() == null)
				continue;
			if (shop.getOwnerPlayer().equals(player))
				return shop;
		}

		return null;
	}


	/**
	 * Get a itemShop by its UUID
	 *
	 * @param itemUUID the uuid of ItemShop
	 * @return
	 */
	public static ItemShop findItem(final Shop shop, final UUID itemUUID) {
		for (final ItemShop itemShop : shop.getItems())
			if (itemShop.uuid.equals(itemUUID))
				return itemShop;

		return null;
	}


	public double getBalance() {
		if (owner == ShopOwnerType.STATE) {
			return DataStorage.getInstance().getNationalFounds();
		}
		return getProfit();
	}

	/**
	 * Get all Shops
	 *
	 * @return
	 */
	public static Collection<Shop> getShops() {
		return Collections.unmodifiableCollection(byName.values());
	}


	/**
	 * Get all LinkedChests
	 */
	public static Collection<Chest> getAllLinkedChests() {
		return Collections.unmodifiableCollection(allLinkedChests.values());
	}


	/**
	 * Get all ItemsUUID
	 *
	 * @return
	 */
	public static Collection<UUID> getItemsUUID(final Shop shop) {
		final Set<UUID> uuids = new HashSet<>();
		for (final ItemShop itemShop : shop.getItems())
			uuids.add(itemShop.getUuid());
		return Collections.unmodifiableCollection(uuids);
	}

}
