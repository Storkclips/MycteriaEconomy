package me.spiderdeluxe.mycteriaeconomy.models.npc;

import lombok.Getter;
import me.spiderdeluxe.mycteriaeconomy.models.shop.Shop;
import me.spiderdeluxe.mycteriaeconomy.models.shop.menu.ShopMenu;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.mineacademy.fo.TimeUtil;

import java.util.UUID;

public class NPCShop extends NPCBase {
	/**
	 * This is the shop connected to the npc
	 */
	@Getter
	private Shop shop;

	public NPCShop(final UUID uuid, final String name, final String shopName, final EntityType type, final net.citizensnpcs.api.npc.NPC citizen) {
		super(uuid, name, type, citizen);

		shop = Shop.alreadyExist(shopName) ? Shop.findShop(shopName) : Shop.createShop(shopName);
		setShopName(shopName);

		setFunction(NPCFunction.SHOP);
	}

	public NPCShop(final UUID uuid, final String npcName, final String shopName, final EntityType type) {
		this(uuid, npcName, shopName, type, null);
	}

	// --------------------------------------------------------------------------------------------------------------
	// NPC Override methods
	// --------------------------------------------------------------------------------------------------------------


	@Override
	protected void onNPCCreation(final NPCBase npcBase) {

		shop.changeCreationDate(TimeUtil.getFormattedDateShort());
	}

	@Override
	protected void onNPCDelete(final String name) {
		shop.deleteShop();
	}

	@Override
	protected void onNPCRightClick(final Player player, final LivingEntity entity, final PlayerInteractEntityEvent event) {
		new ShopMenu(shop).displayTo(player);
	}

}

