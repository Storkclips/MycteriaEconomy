package me.spiderdeluxe.mycteriaeconomy.models.npc;

import me.spiderdeluxe.mycteriaeconomy.MycteriaEconomyPlugin;
import me.spiderdeluxe.mycteriaeconomy.models.npc.operator.NPCOperation;
import me.spiderdeluxe.mycteriaeconomy.models.npc.operator.NPCOperator;
import me.spiderdeluxe.mycteriaeconomy.models.shop.Shop;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import me.spiderdeluxe.mycteriaeconomy.util.SFXManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.Remain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class NPCListener implements Listener {
	// Stores the players that have been asked to confirm the deletion of NPCs or Shop Items
	private final Set<UUID> deleteConfirmations = new HashSet<>();

	@EventHandler
	public void onCombust(final EntityCombustEvent event) {
		final NPCBase npcBase = NPCBase.findNPC(event.getEntity());

		if (npcBase != null)
			event.setCancelled(true);
	}

	@EventHandler
	public void onDeath(final EntityDeathEvent event) {
		final NPCBase npcBase = NPCBase.findNPC(event.getEntity());

		if (npcBase != null) {
			final List<ItemStack> drops = event.getDrops();
			final LivingEntity entity = event.getEntity();

			// Handle riding entity
			final Entity vehicle = entity.getVehicle();

			if (vehicle != null)
				vehicle.remove();

			npcBase.onDeath(entity.getKiller(), entity, event);
		}
	}

	@EventHandler
	public void onEntityDamage(final EntityDamageByEntityEvent event) {
		final Entity damager = event.getDamager();
		final Entity victim = event.getEntity();

		if (!(damager instanceof LivingEntity) || !(victim instanceof LivingEntity))
			return;

		NPCBase npcBase = NPCBase.findNPC(damager);

		if (npcBase != null) {
			npcBase.onAttack((LivingEntity) damager, (LivingEntity) victim, event);
		}

		npcBase = NPCBase.findNPC(victim);

		if (npcBase != null) {

			npcBase.onDamaged((LivingEntity) damager, (LivingEntity) victim, event);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityInteract(final PlayerInteractEntityEvent event) {
		if (!Remain.isInteractEventPrimaryHand(event))
			return;

		final Entity clickedEntity = event.getRightClicked();
		final Player player = event.getPlayer();

		if (!(clickedEntity instanceof LivingEntity))
			return;

		if (NPCBase.findNPC(clickedEntity) == null) {
			return;
		}

		final NPCBase npcBase = NPCBase.findNPC(clickedEntity);


		if (NPCOperator.getByPlayer(player) != null) {

			final NPCOperator npcOperator = NPCOperator.getByPlayer(player);
			assert npcOperator != null;
			final NPCOperation operation = npcOperator.getOperation();
			if (operation == NPCOperation.DELETE) {
				deleteNpc(player, npcBase);
				return;
			}
			if (operation == NPCOperation.LINK && npcBase instanceof NPCShop) {
				final Shop shop = ((NPCShop) npcBase).getShop();

				final Chest chest = npcOperator.getLinkingChest();
				if (shop.isChestLinked(chest)) {
					Messager.sendErrorMessage(player, "&cThis chest is already linked with this NPC.");
					return;
				}
				shop.linkChest(npcOperator.getLinkingChest());
				Messager.sendMessage(player, "&aChest successfully linked.");
				SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 0.6F, 1.4F);
				return;
			}
			return;
		}

			npcBase.onRightClick(player, (LivingEntity) clickedEntity, event);
	}

	/**
	 * Attemps to delete the passed NPC, if the player hasn't been asked for confirmation ask for it, if it has then
	 * directly delete the NPC.
	 *
	 * @param player  Player that is deleting the NPCAtm.
	 * @param npcBase NPC that is being deleted.
	 */
	private void deleteNpc(final Player player, final NPCBase npcBase) {
		final UUID playerUuid = player.getUniqueId();
		if (!deleteConfirmations.contains(playerUuid)) {
			deleteConfirmations.add(playerUuid);
			Bukkit.getScheduler().runTaskLater(MycteriaEconomyPlugin.getInstance(),
					() -> deleteConfirmations.remove(playerUuid), 100L);
			Messager.sendMessage(player, "&eRight click again to confirm the deletion of this NPCBase.");
			SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.6F, 1.4F);
			return;
		}
		deleteConfirmations.remove(playerUuid);
		npcBase.deleteNPC();
		SFXManager.playPlayerSound(player, Sound.BLOCK_GRAVEL_PLACE, 0.6F, 1.9F);
	}


	private ItemStack buildUndamagedItem(final ItemCreator.ItemCreatorBuilder item) {
		return item.damage(0).build().makeSurvival();
	}
}
