package me.spiderdeluxe.mycteriaeconomy.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.spiderdeluxe.mycteriaeconomy.models.npc.NPCAtm;
import me.spiderdeluxe.mycteriaeconomy.models.npc.NPCBase;
import me.spiderdeluxe.mycteriaeconomy.models.npc.NPCFunction;
import me.spiderdeluxe.mycteriaeconomy.models.npc.NPCShop;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.EntityType;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.settings.YamlSectionConfig;

import java.util.*;

public class NPCCache extends YamlSectionConfig {

	private static final Map<UUID, NPCCache> cacheMap = new HashMap<>();

	@Getter
	private final List<ActiveNPC> activeNPCS = new ArrayList<>();

	/**
	 * Create a new section config with a section prefix,
	 * in this case the UUID of generator.
	 */
	public NPCCache(final String uuid) {
		super(uuid);

		loadConfiguration(null, "npc.db");
	}


	@Override
	protected void onLoadFinish() {
		activeNPCS.clear();

		if (isSet("NPC")) {
			// It is basically stored as a list however each list item is a hashmap
			// containing keys and values
			final SerializedMap npcSettings = getMap("NPC");

			final UUID uuid = npcSettings.getUUID("UUID");
			final String name = npcSettings.getString("Name");
			final EntityType entityType = EntityType.valueOf(npcSettings.getString("EntityType"));
			net.citizensnpcs.api.npc.NPC citizen = CitizensAPI.getNPCRegistry().getByUniqueId(npcSettings.getUUID("Citizen"));
			if (citizen == null)
				citizen = CitizensAPI.getNPCRegistry().getById(npcSettings.getInteger("Citizen_ID"));
			final NPCFunction function = NPCFunction.fromName(npcSettings.getString("Function"));

			if (function == NPCFunction.SHOP) {
				final String shopName = npcSettings.getString("Shop_Name");
				activeNPCS.add(new ActiveNPC(new NPCShop(uuid, name, shopName, entityType, citizen)));
			} else {
				final UUID atmUUID = npcSettings.getUUID("Atm_Uuid");
				if (atmUUID == uuid) {
					activeNPCS.add(new ActiveNPC(new NPCAtm(name, atmUUID, entityType, citizen)));
				}
			}
		}
	}

	//------------------------------------------------------------------
	// Region manipulation
	//------------------------------------------------------------------
	public void addNPC(final NPCBase npcBase) {
		Valid.checkNotNull(npcBase, "This npc doesn't exists");

		activeNPCS.add(new ActiveNPC(npcBase));
		save("NPC", new ActiveNPC(npcBase));
	}

	public void removeNPC(final NPCBase npcBase) {
		final ActiveNPC activeNPC = getActiveNPCS(npcBase);

		activeNPCS.remove(activeNPC);
		save("", null);
	}
	//------------------------------------------------------------------
	// getCache methods
	//------------------------------------------------------------------


	public static NPCCache getCache(final NPCBase npcBase) {
		return getCache(npcBase.getUuid());
	}

	public static NPCCache getCache(final UUID uuid) {
		NPCCache cache = cacheMap.get(uuid);

		if (cache == null) {
			cache = new NPCCache(uuid.toString());

			cacheMap.put(uuid, cache);
		}

		return cache;
	}


	public ActiveNPC getActiveNPCS(final NPCBase npcBase) {
		for (final ActiveNPC activeNPC : activeNPCS)
			if (activeNPC.getNpcBase().getUuid().equals(npcBase.getUuid()))
				return activeNPC;

		return null;
	}


	//------------------------------------------------------------------
	// Deserialization method
	//------------------------------------------------------------------


	// This file holds information about active npcShop
	@Getter
	@AllArgsConstructor
	public class ActiveNPC implements ConfigSerializable {

		// The Npc
		private NPCBase npcBase;

		// This method does the magic when you use save() method above
		// and converts this class into a hash map you can save in your yml files
		@Override
		public SerializedMap serialize() {
			final SerializedMap map = new SerializedMap();

			//Information
			map.put("UUID", npcBase.getUuid());
			map.put("Name", npcBase.getName());
			map.put("EntityType", npcBase.getType());
			map.put("Citizen", npcBase.getCitizen().getUniqueId());
			map.put("Citizen_ID", npcBase.getCitizen().getId());
			map.put("Function", npcBase.getFunction());

			if (npcBase.getShopName() != null)
				map.put("Shop_Name", npcBase.getShopName());
			if (npcBase.getAtmUUID() != null)
				map.put("ATM_Uuid", npcBase.getAtmUUID());
			return map;
		}
	}
}
