package me.spiderdeluxe.mycteriaeconomy.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;

import java.util.Objects;

// This file holds information about the linkedChest
@Getter
@AllArgsConstructor
public class LinkedChest implements ConfigSerializable {

	Block chestBlock;

	// This method does the magic when you use save() method above
	// and converts this class into a hash map you can save in your yml files
	@Override
	public SerializedMap serialize() {
		final SerializedMap map = new SerializedMap();

		map.put("Location", chestBlock.getLocation());

		return map;
	}

	public static LinkedChest deserialize(final SerializedMap map) {

	final Location location = getLocation("Location", map);

		return new LinkedChest(location.getWorld().getBlockAt(location));
	}
	//------------------------------------------------------------------
	// Deserialization method
	//------------------------------------------------------------------

	public static Location getLocation(final String key, final SerializedMap settings) {
		final String[] parts = Objects.requireNonNull(settings.getString(key)).split(" ");
		return new Location(Bukkit.getServer().getWorld(parts[0]), Integer.parseInt(parts[1]),
				Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));

	}
}
