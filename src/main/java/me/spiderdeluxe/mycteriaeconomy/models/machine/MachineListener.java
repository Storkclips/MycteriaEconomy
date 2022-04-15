package me.spiderdeluxe.mycteriaeconomy.models.machine;

import me.spiderdeluxe.mycteriaeconomy.models.shop.Shop;
import me.spiderdeluxe.mycteriaeconomy.models.shop.ShopListener;
import me.spiderdeluxe.mycteriaeconomy.conversation.shop.ShopSetupPrompt;
import me.spiderdeluxe.mycteriaeconomy.menu.shop.ShopMenu;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import me.spiderdeluxe.mycteriaeconomy.util.SFXManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.ItemUtil;

public class MachineListener implements Listener {


	/**
	 * Listens when a player places a block and checks if the block is a Trading or Vending machine, if it is then
	 * install and register it to the local database.
	 *
	 * @param event BlockPlaceEvent
	 */
	@EventHandler
	public void onPlayerInstallMachine(final BlockPlaceEvent event) {
		if (event.isCancelled()) return;
		final Player player = event.getPlayer();
		final Block placedBlock = event.getBlockPlaced();
		final ItemStack handItem = event.getItemInHand();
		if (ItemUtil.isSimilar(handItem, Machine.getItemStack())) {
			new ShopSetupPrompt(placedBlock.getLocation()).show(player);
		}
	}


	/**
	 * Listens when a player right clicks a block, if the block is any sort of Machine then open the respective
	 * GUI.
	 * <p>
	 * If the player normally right clicks it will open the shop GUI.
	 * If the player Shift + Right clicks and the player is the owner of the machine open the Owner GUI.
	 *
	 * @param event PlayerInteractEvent
	 */
	@EventHandler
	public void onBlockRightClick(final PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		final Player player = event.getPlayer();
		final Block clickedBlock = event.getClickedBlock();
		final Material clickedBlockType = clickedBlock.getType();
		if ((clickedBlockType == Material.DISPENSER || clickedBlockType == Material.DROPPER)) {
			// Opening Machine
			final Machine clickedMachine = Machine.findMachine(clickedBlock.getLocation());
			if (clickedMachine == null) return;

			event.setCancelled(true);


			new ShopMenu(clickedMachine.getShop()).displayTo(player);
			SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 0.6F, 1.7F);
			return;
		}
		if (clickedBlockType != Material.CHEST && clickedBlockType != Material.TRAPPED_CHEST) return;
		final BlockState blockState = clickedBlock.getState();
		if (!(blockState instanceof Chest)) return;


		// Linking Machine to chest
		final Chest chest = (Chest) blockState;

		final Shop linkingShop = ShopListener.findShopByPlayerLinkers(player);

		if (!ShopListener.isLinkers(player))
			return;

		event.setCancelled(true);
		if (linkingShop != null) {
			if (linkingShop.isChestLinked(chest)) {
				Messager.sendErrorMessage(player, "&cThis chest is already linked to your machine.");
			} else {
				linkingShop.linkChest(chest);
				Messager.sendSuccessMessage(player, "&aChest successfully linked.");
			}
		}
	}
}
