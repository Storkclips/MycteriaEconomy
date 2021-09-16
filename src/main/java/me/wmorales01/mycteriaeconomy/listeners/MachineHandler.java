package me.wmorales01.mycteriaeconomy.listeners;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.AbstractMachine;
import me.wmorales01.mycteriaeconomy.models.TradingMachine;
import me.wmorales01.mycteriaeconomy.models.VendingMachine;
import me.wmorales01.mycteriaeconomy.util.InventoryUtil;
import me.wmorales01.mycteriaeconomy.util.Messager;
import me.wmorales01.mycteriaeconomy.util.SFXManager;
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

import java.util.UUID;

public class MachineHandler implements Listener {
    private final MycteriaEconomy plugin;

    public MachineHandler(MycteriaEconomy plugin) {
        this.plugin = plugin;
    }

    /**
     * Listens when a player places a block and checks if the block is a Trading or Vending machine, if it is then
     * install and register it to the local database.
     *
     * @param event BlockPlaceEvent
     */
    @EventHandler
    public void onPlayerInstallMachine(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        Block placedBlock = event.getBlockPlaced();
        ItemStack handItem = event.getItemInHand();
        AbstractMachine installedMachine;
        String installMessage;
        if (InventoryUtil.isSimilar(handItem, VendingMachine.getItemStack())) { // Installing Vending Machine
            installedMachine = new VendingMachine(player, placedBlock);
            installMessage = "&eVending Machine successfully installed.";
        } else if (InventoryUtil.isSimilar(handItem, TradingMachine.getItemStack())) { // Installing Trading Machine
            installedMachine = new TradingMachine(player, placedBlock);
            installMessage = "&eTrading Machine successfully installed.";
        } else {
            return;
        }
        installedMachine.registerMachine();
        Messager.sendMessage(player, installMessage);
        SFXManager.playWorldSound(placedBlock.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.8F, 1.4F);
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
    public void onBlockRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        Material clickedBlockType = clickedBlock.getType();
        if (clickedBlockType == Material.DISPENSER || clickedBlockType == Material.DROPPER) {
            // Opening Machine
            AbstractMachine clickedMachine = AbstractMachine.fromLocation(clickedBlock.getLocation());
            if (clickedMachine == null) return;

            event.setCancelled(true);
            if (!player.isSneaking()) {
                player.openInventory(clickedMachine.getShopGUI());
                SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 0.6F, 1.7F);
                return;
            }
            if (!clickedMachine.isOwner(player)) return;

            player.openInventory(clickedMachine.getOwnerGUI());
            SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 0.6F, 1.7F);
            return;
        }
        if (clickedBlockType != Material.CHEST && clickedBlockType != Material.TRAPPED_CHEST) return;
        BlockState blockState = clickedBlock.getState();
        if (!(blockState instanceof Chest)) return;

        // Linking Machine to chest
        UUID playerUuid = player.getUniqueId();
        Chest chest = (Chest) blockState;
        if (!plugin.getMachineLinkers().containsKey(playerUuid)) return;

        event.setCancelled(true);
        AbstractMachine linkingMachine = plugin.getMachineLinkers().get(playerUuid);
        if (linkingMachine.isChestLinked(chest)) {
            Messager.sendErrorMessage(player, "&cThis chest is already linked to your machine.");
            return;
        }
        linkingMachine.linkChest(chest);
        Messager.sendSuccessMessage(player, "&aChest successfully linked.");
    }
}
