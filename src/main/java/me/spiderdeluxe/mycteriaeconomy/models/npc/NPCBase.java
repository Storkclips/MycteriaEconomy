package me.spiderdeluxe.mycteriaeconomy.models.npc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.spiderdeluxe.mycteriaeconomy.cache.ListStorage;
import me.spiderdeluxe.mycteriaeconomy.cache.NPCCache;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.MemoryDataKey;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.CompProperty;
import org.mineacademy.fo.remain.Remain;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Represents the core model for an NPCBase,
 */
public class NPCBase {

	/**
	 * The persistent NBT tag we use to mark an entity as our NPCBase
	 */
	public static final String NPC_TAG = "MYCTERIA_NPC";

	/**
	 * Stores active npc by their name, so they are singletons
	 */
	private static final Map<UUID, NPCBase> byUUID = new HashMap<>();


	/**
	 * The NPC registry from Citizens plugin
	 */
	@Getter
	public static final NPCRegistry citizens = CitizensAPI.getNPCRegistry();


	/**
	 * The entity rapresented by the Citizens model
	 * <p>
	 * This is null until makeEntity is called when spawning!
	 */
	@Getter
	private NPC citizen;


	/**
	 * The uuid of this npc
	 */
	@Getter
	private UUID uuid;

	/**
	 * The name of this npc
	 */
	@Getter
	private final String name;

	/**
	 * The location of this npc
	 */
	@Getter
	@Setter
	private Location npcLoc;

	/**
	 * The entity type that this npc represents in Minecraft (client can only render existing entities)
	 */
	@Getter
	private final EntityType type;

	/**
	 * The NPCBase prefix when using tell methods below
	 */
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PROTECTED)
	private String tellPrefix;

	/**
	 * Has AI? Moves around, gravity etc.
	 */
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PROTECTED)
	private boolean ai = false;


	/**
	 * This is the function of the NPCBase
	 */
	@Getter(AccessLevel.PUBLIC)
	@Setter(AccessLevel.PUBLIC)
	private NPCFunction function;


	/**
	 * This field is only for caching and is used in the NPCAtm class
	 */
	@Getter
	@Setter
	private String shopName;

	/**
	 * This field is only for caching and is used in the NPCAtm class
	 */
	@Getter
	@Setter
	private UUID atmUUID;

	/**
	 * Create a new NPCBase with the given name of the given type
	 * <p>
	 * NB: DO NOT TYPE IN PLAYER ENTITY TYPE THERE, IT WONT WORK!
	 * PLAYER NPCBase REQUIRE CITIZENS LIBRARY THAT WE COVER IN WEEK 4
	 *
	 * @param name
	 * @param type
	 */
	public NPCBase(UUID uuid, final String name, final EntityType type, final net.citizensnpcs.api.npc.NPC citizen) {
		//	Valid.checkBoolean(type.isAlive() && type.isSpawnable(), "The boss type must be alive and spawnable!"); // Edit: I placed it here so that you see errors faster
		if (uuid == null)
			uuid = createNPCUUID();

		this.uuid = uuid;
		this.name = name;
		this.type = type;
		this.citizen = citizen;

		byUUID.put(uuid, this);
	}

	public NPCBase(UUID uuid, final String name, final EntityType type) {
		//	Valid.checkBoolean(type.isAlive() && type.isSpawnable(), "The boss type must be alive and spawnable!"); // Edit: I placed it here so that you see errors faster
		if (uuid == null)
			uuid = createNPCUUID();

		this.uuid = uuid;
		this.name = name;
		this.type = type;

		byUUID.put(uuid, this);
	}

	// --------------------------------------------------------------------------------------------------------------
	// Manipulation events
	// --------------------------------------------------------------------------------------------------------------
	public static void loadAll() {
		byUUID.clear();
		ListStorage.getInstance().loadConfiguration(null, "data.db");

		for (final String npcUUID : ListStorage.getInstance().getNpcList()) {

			final NPCCache cache = NPCCache.getCache(UUID.fromString(npcUUID));

			cache.loadConfiguration(null, "npc.db");

			for (final NPCCache.ActiveNPC activeNPC : cache.getActiveNPCS())
				byUUID.put(activeNPC.getNpcBase().getUuid(), activeNPC.getNpcBase());
		}
		Common.log("Active NPC: " + byUUID.size());
	}


	public void createNPC(final Location loc, final String modelName) throws NPCLoadException {
		final UUID uuid = createNPCUUID();
		final NPCBase npcBase = this;
		npcLoc = loc;

		npcBase.spawn(loc, modelName);

		final NPCCache npcCache = NPCCache.getCache(npcBase);
		npcCache.addNPC(npcBase);

		final ListStorage storage = ListStorage.getInstance();
		storage.addNPC(npcBase);

		byUUID.put(uuid, npcBase);

		onNPCCreation(npcBase);
	}

	public void deleteNPC() {
		final NPCBase npcBase = this;

		if (npcBase.citizen != null)
			npcBase.citizen.destroy();


		final NPCCache npcCache = NPCCache.getCache(npcBase);
		npcCache.removeNPC(npcBase);

		final ListStorage storage = ListStorage.getInstance();
		storage.removeNPC(npcBase);

		byUUID.remove(npcBase.getUuid(), npcBase);

		onNPCDelete(name);
	}


	/**
	 * Create a uuid to identify the bound by verifying that there is no identical one already present
	 *
	 * @return the ATM's uuid
	 */
	public static UUID createNPCUUID() {
		UUID uuid = UUID.randomUUID();
		while (getNPCs().contains(uuid)) {
			uuid = UUID.randomUUID();
		}
		return uuid;
	}


	/**
	 * Spawn this boss at the given location and apply all properties
	 * in this class for him
	 *
	 * @param location
	 */
	public final void spawn(final Location location, final String modelName) throws NPCLoadException {
		final LivingEntity entity = makeEntity(location, modelName);

		Remain.setCustomName(entity, name);

		// Mark that boss with our persistent NBT tag
		CompMetadata.setMetadata(entity, NPC_TAG, name);

		if (entity.isValid() && !entity.isDead())
			onSpawn(location, entity);
	}

	/**
	 * Create a new NPCBase using the Citizens plugin
	 */
	public LivingEntity makeEntity(final Location location, final String modelName) throws NPCLoadException {
		this.citizen = citizens.createNPC(getType(), getName());

		if (modelName != null) {
			final Trait trait = CitizensAPI.getTraitFactory().getTrait("model");
			if (trait != null) {
				citizen.addTrait(trait);

				final DataKey dataKey = new MemoryDataKey();
				dataKey.setString("modelId", modelName);
				dataKey.setBoolean("showName", true);
				trait.load(dataKey);
			}
		}

		final boolean spawned = citizen.spawn(location);
		final Entity entity = citizen.getEntity();

		Valid.checkBoolean(spawned, "Unable to spawn " + getName() + " at " + Common.shortLocation(location));
		Valid.checkBoolean(entity instanceof LivingEntity, "NPCBase " + getName() + " must be LivingEntity, got " + entity.getClass());

		return (LivingEntity) entity;
	}

	// --------------------------------------------------------------------------------------------------------------
	// Handling events
	// --------------------------------------------------------------------------------------------------------------

	/**
	 * Called automatically after the NPCBase has been spawned
	 *
	 * @param location
	 * @param entity
	 */
	protected final void onSpawn(final Location location, final LivingEntity entity) {

		// Prevent NPCBase from moving
		if (!ai)
			CompProperty.AI.apply(entity, false);

		// God mode for NPCBase - it can still be killed by other means though
		CompProperty.INVULNERABLE.apply(entity, true);

		// Disable NPCBase making sounds
		CompProperty.SILENT.apply(entity, true);

		// Should prevent despawning
		entity.setRemoveWhenFarAway(false);

		onNPCSpawn(location, entity);
	}

	/**
	 * Called after the NPCBase is spawned and its AI disabled
	 *
	 * @param location
	 * @param entity
	 */
	protected void onNPCSpawn(final Location location, final LivingEntity entity) {
	}


	/**
	 * Called after the NPCBase is been created
	 */
	protected void onNPCCreation(final NPCBase npcBase) {
	}


	/**
	 * Called after the NPCBase is been deleted
	 */
	protected void onNPCDelete(final String name) {
	}


	/**
	 * Called automatically when a player right clicks this NPCBase
	 *
	 * @param player
	 * @param entity
	 * @param event
	 */
	protected void onNPCRightClick(final Player player, final LivingEntity entity, final PlayerInteractEntityEvent event) {
		tell(player, "piedi");
	}

	/**
	 * Called when the Npc dies
	 *
	 * @param killer    the player killer or null
	 * @param npcEntity the NPCBase entity
	 * @param event
	 */
	public void onDeath(@Nullable final Player killer, final LivingEntity npcEntity, final EntityDeathEvent event) {
	}

	/**
	 * Called when the NPCBase attacks something
	 *
	 * @param npcAttacker
	 * @param victim
	 * @param event
	 */
	public void onAttack(final LivingEntity npcAttacker, final LivingEntity victim, final EntityDamageByEntityEvent event) {
	}

	/**
	 * Called automatically from NPCBase timed task
	 *
	 * @param npcEntity
	 */
	public void onTick(final LivingEntity npcEntity) {
	}

	/**
	 * Called automatically when a player right clicks this NPCBase
	 *
	 * @param player
	 * @param npc
	 * @param event
	 */
	public final void onRightClick(final Player player, final LivingEntity npc, final PlayerInteractEntityEvent event) {

		// Or, prevent quest from catching this right click and handle it here
		event.setCancelled(true);

		onNPCRightClick(player, npc, event);
	}


	/**
	 * Prevent NPCs from taking damage
	 *
	 * @param attacker
	 * @param bossVictim
	 * @param event
	 */
	public final void onDamaged(final LivingEntity attacker, final LivingEntity bossVictim, final EntityDamageByEntityEvent event) {
		event.setCancelled(true);
	}


	// --------------------------------------------------------------------------------------------------------------
	// Convenience methods
	// --------------------------------------------------------------------------------------------------------------

	/**
	 * Convenience method for sending a message to the player
	 * using {@link #tellPrefix}
	 *
	 * @param player
	 * @param message
	 */
	public final void tell(final Player player, final String message) {
		Common.tellNoPrefix(player, Common.getOrEmpty(tellPrefix) + message);
	}

	/**
	 * Convenience method for sending a non-repetitive message to the player,
	 * see {@link Common#tellTimed(int, org.bukkit.command.CommandSender, String)}
	 * using the {@link #tellPrefix}
	 *
	 * @param delaySeconds
	 * @param player
	 * @param message
	 */
	public final void tellTimed(final int delaySeconds, final Player player, final String message) {
		Common.tellTimedNoPrefix(delaySeconds, player, Common.getOrEmpty(tellPrefix) + message);
	}

	// --------------------------------------------------------------------------------------------------------------
	// Configurable npc attributes
	// --------------------------------------------------------------------------------------------------------------

	/**
	 * Return true if the given object is a boss having the same name
	 *
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(final Object obj) {
		return obj instanceof NPCBase && ((NPCBase) obj).getName().equals(getName());
	}


	// --------------------------------------------------------------------------------------------------------------
	// Static access
	// --------------------------------------------------------------------------------------------------------------

	/**
	 * If the given entity has our NBT tag, see {@link #NPC_TAG},
	 * then return the boss if we have it registered.
	 *
	 * @param entity
	 * @return
	 */
	public static NPCBase findNPC(final Entity entity) {
		if (!(entity instanceof LivingEntity) || !HookManager.isNPC(entity))
			return null;

		final NPC citizen = citizens.getNPC(entity);
		for (final NPCBase npcBase : getNPCs()) {
			if(npcBase.citizen == null)
				continue;

			if (npcBase.citizen == citizen
					|| npcBase.citizen.getUniqueId().equals(citizen.getUniqueId()))
				return npcBase;
		}
		return null;
	}

	/**
	 * Get the npc by its name
	 *
	 * @param name
	 * @return
	 */
	public static NPCBase findNPC(final String name) {
		Valid.checkNotNull(name);

		for (final NPCBase npcBase : getNPCs())
			if (npcBase.getName().equals(name))
				return npcBase;

		return null;
	}

	/**
	 * Get all bosses
	 *
	 * @return
	 */
	public static Collection<NPCBase> getNPCs() {
		return Collections.unmodifiableCollection(byUUID.values());
	}

	/**
	 * Return all atms as their UUID
	 *
	 * @return
	 */
	public static Set<UUID> getNPCsUUID() {
		return Collections.unmodifiableSet(byUUID.keySet());
	}

}