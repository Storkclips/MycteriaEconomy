package me.spiderdeluxe.mycteriaeconomy.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.spiderdeluxe.mycteriaeconomy.models.machine.Machine;
import me.spiderdeluxe.mycteriaeconomy.models.shop.Shop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.collection.expiringmap.ExpiringMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.settings.YamlSectionConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class PlayerCache extends YamlSectionConfig {

	private static final ExpiringMap<UUID, PlayerCache> cacheMap = ExpiringMap.builder().expiration(30, TimeUnit.MINUTES).build();

	/**
	 * This is the uuid of a player
	 */
	private final UUID uuid;

	/**
	 * It stores the set of player's machine
	 */
	@Getter
	@Setter
	private Set<ActiveMachine> machines = new HashSet<>();


	public PlayerCache(final UUID uuid) {

		// This will prepend this cache with the players unique id just like you use pathPrefix in SimpleSettings
		super(uuid.toString());

		this.uuid = uuid;

		// Load our player cache from the disk however do not use any default file
		// from our source code
		loadConfiguration(NO_DEFAULT, "player.db");
	}

	@Override
	protected void onLoadFinish() {
		machines.clear();

		if (isSet("Machines")) {
			// It is basically stored as a list however each list item is a hashmap
			// containing keys and values
			final List<SerializedMap> machinesList = getMapList("Machines");

			for (final SerializedMap machinesSettings : machinesList) {

				final UUID machineUUID = machinesSettings.getUUID("UUID");
				final Location location = getLocation("Location", machinesSettings);

				final String shopName = machinesSettings.getString("Shop");
				final Shop shop = Shop.alreadyExist(shopName) ? Shop.findShop(shopName) : Shop.createShop(shopName);

				final OfflinePlayer owner = Bukkit.getOfflinePlayer(uuid);
				machines.add(new ActiveMachine(new Machine(owner, location, shop, machineUUID)));
			}
		}
	}

	public void addMachine(final Machine machine) {

		machines.add(new ActiveMachine(machine));
		save("Machines", machines);
	}

	public void removeMachine(final Machine machine) {
		Valid.checkNotNull(machine, "This machine doesn't exists");

		final ActiveMachine activeMachine = getMachine(machine);

		machines.remove(activeMachine);
		save("Machines", machines);
	}


	/**
	 * This method is used to find an ActiveMachine from a Machine
	 *
	 * @param machine
	 * @return
	 */
	public ActiveMachine getMachine(final Machine machine) {
		for (final ActiveMachine activeMachine : machines)
			if (activeMachine.getMachine().getUuid().equals(machine.getUuid()))
				return activeMachine;

		return null;
	}


	// --------------------------------------------------------------------------------------------------------------
	// Static methods below
	// --------------------------------------------------------------------------------------------------------------

	public static PlayerCache getCache(final OfflinePlayer player) {
		return getCache(player.getUniqueId());
	}

	public static PlayerCache getCache(final UUID uuid) {
		PlayerCache cache = cacheMap.get(uuid);

		if (cache == null) {
			cache = new PlayerCache(uuid);

			cacheMap.put(uuid, cache);
		}

		return cache;
	}

	// --------------------------------------------------------------------------------------------------------------
	// Own classes
	// --------------------------------------------------------------------------------------------------------------

	// This file holds information about the players class
	@Getter
	@AllArgsConstructor
	public static final class ActiveMachine implements ConfigSerializable {

		// The machine the player has
		private final Machine machine;

		// This method does the magic when you use save() method above
		// and converts this class into a hash map you can save in your yml files
		@Override
		public SerializedMap serialize() {
			final SerializedMap map = new SerializedMap();

			map.put("UUID", machine.getUuid());
			map.put("Location", machine.getLocation());
			map.put("Shop", machine.getShop().getName());

			return map;
		}


	}
	//------------------------------------------------------------------
	// Deserialization method
	//------------------------------------------------------------------

	public static Location getLocation(final String key, final SerializedMap settings) {
		final String[] parts = (settings.getString(key)).split(" ");
		return new Location(Bukkit.getServer().getWorld(parts[0]), Integer.parseInt(parts[1]),
				Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));

	}

}
