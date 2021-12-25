package me.spiderdeluxe.mycteriaeconomy.models.machine;

import lombok.Getter;
import me.spiderdeluxe.mycteriaeconomy.cache.ListStorage;
import me.spiderdeluxe.mycteriaeconomy.cache.PlayerCache;
import me.spiderdeluxe.mycteriaeconomy.models.shop.Shop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.*;

/**
 * @author SpiderDeluxe
 * This class deals with the management of machine
 */
public class Machine {
	/**
	 * Stores active machine by their UUID, machine are singletons
	 */
	@Getter
	private static final Map<UUID, Machine> byUUID = new HashMap<>();

	/**
	 * The uuid of this Machine
	 */
	@Getter
	private UUID uuid;

	/**
	 * The shop connected to this Machine
	 */
	@Getter
	private Shop shop;


	/**
	 * The owner player of this Machine
	 */
	@Getter
	private OfflinePlayer player;

	/**
	 * The location of this machine
	 */
	@Getter
	private Location location;

	public Machine(final OfflinePlayer player, final Location location, final Shop shop, final UUID uuid) {
		this.location = location;
		this.shop = shop;
		this.player = player;
		this.uuid = uuid;
	}


	public static void loadAll() {
		byUUID.clear();
		ListStorage.getInstance().loadConfiguration(null, "data.db");

		for (final String machineUUID : ListStorage.getInstance().getMachinesList()) {
			final PlayerCache cache = PlayerCache.getCache(UUID.fromString(machineUUID));

			for (final PlayerCache.ActiveMachine activeMachine : cache.getMachines())
				byUUID.put(activeMachine.getMachine().getUuid(), activeMachine.getMachine());
		}
		Common.log("Active Machine: " + byUUID.size());
	}

	// --------------------------------------------------------------------------------------------------------------
	// Machine manipulation static access
	// --------------------------------------------------------------------------------------------------------------

	public static void createMachine(final Player player, final String shopName, final Location location) {

		final Shop shop = Shop.createShop(player, shopName);
		final UUID uuid = Machine.createMachineUUID();

		final Machine machine = new Machine(player, location, shop, uuid);

		final ListStorage storage = ListStorage.getInstance();
		storage.addMachine(player);

		final PlayerCache cache = PlayerCache.getCache(player);
		cache.addMachine(machine);

		byUUID.put(uuid, machine);
	}

	public void deleteMachine() {
		final Machine machine = this;

		shop.deleteShop();

		final PlayerCache cache = PlayerCache.getCache(player);
		cache.removeMachine(machine);

		final ListStorage storage = ListStorage.getInstance();
		storage.removeMachine(machine);

		byUUID.remove(uuid, machine);
	}

	/**
	 * Create a uuid to identify the bound by verifying that there is no identical one already present
	 *
	 * @return the ATM's uuid
	 */
	public static UUID createMachineUUID() {
		UUID uuid = UUID.randomUUID();
		while (getMachinesUUID().contains(uuid)) {
			uuid = UUID.randomUUID();
		}
		return uuid;
	}


	// --------------------------------------------------------------------------------------------------------------
	// Machine static access
	// --------------------------------------------------------------------------------------------------------------

	/**
	 * Returns the ItemStack corresponding to a Machine
	 *
	 * @return the ItemStack of a Machine
	 */
	public static ItemStack getItemStack() {
		return ItemCreator.of(CompMaterial.DISPENSER)
				.name("Commercial Machine")
				.lore("&ePlace this block to install a Machine.")
				.build().make();
	}

	// --------------------------------------------------------------------------------------------------------------
	// Utility Methods
	// --------------------------------------------------------------------------------------------------------------

	/**
	 * Check if a block is a machineBlock
	 *
	 * @param block the block
	 */
	public static boolean isMachineBlock(final Block block) {
		final Material type = block.getType();
		if (type == Material.DISPENSER)
			return Machine.findMachine(block.getLocation()) != null;
		return false;
	}


	/**
	 * Check if already exist an Machine with this uuid
	 *
	 * @param uuid the uuid of atm
	 */
	public static boolean alreadyExist(final UUID uuid) {
		return findMachine(uuid) != null;
	}
	// --------------------------------------------------------------------------------------------------------------
	// Static access
	// --------------------------------------------------------------------------------------------------------------


	/**
	 * Get the Machine by its uuid
	 *
	 * @param uuid the uuid of Machine
	 */
	public static Machine findMachine(final UUID uuid) {
		for (final Machine machine : getMachines())
			if (machine.getUuid().equals(uuid))
				return machine;

		return null;
	}

	/**
	 * Get the Machine by its Location
	 *
	 * @param location the location of Machine
	 */
	public static Machine findMachine(final Location location) {
		for (final Machine machine : getMachines())
			if (machine.getLocation().equals(location))
				return machine;

		return null;
	}


	/**
	 * Return all Machines
	 *
	 * @return
	 */
	public static Collection<Machine> getMachines() {
		return Collections.unmodifiableCollection(byUUID.values());
	}

	/**
	 * Return all Machines as their UUID
	 *
	 * @return
	 */
	public static Set<UUID> getMachinesUUID() {
		return Collections.unmodifiableSet(byUUID.keySet());
	}

}
