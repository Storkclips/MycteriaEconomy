package me.wmorales01.mycteriaeconomy.listeners;

import com.mojang.datafixers.util.Pair;
import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.events.NPCRightClickEvent;
import me.wmorales01.mycteriaeconomy.files.ConfigManager;
import me.wmorales01.mycteriaeconomy.models.AbstractNPCShop;
import me.wmorales01.mycteriaeconomy.models.NPCShopOperation;
import me.wmorales01.mycteriaeconomy.models.NPCShopOperator;
import me.wmorales01.mycteriaeconomy.util.Messager;
import me.wmorales01.mycteriaeconomy.util.SFXManager;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * This class handles all the operations relating NCPC Shops.
 */
public class NPCSHopHandler implements Listener {
    private final MycteriaEconomy plugin;
    // Stores the players that have been asked to confirm the deletion of NPCs or Shop Items
    private final Set<UUID> deleteConfirmations = new HashSet<>();

    public NPCSHopHandler(MycteriaEconomy plugin) {
        this.plugin = plugin;
    }

    /**
     * Listens when a player moves, if the player is within the configured distance of an NPC then move the NPC's head to
     * look at the player's locaton.
     *
     * @param event PlayerMoveEvent.
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;
        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) return;

        moveNearNpcHeads(event.getPlayer());
    }

    /**
     * Looks for the NPCs near to the player and moves their head to look at the player's location.
     *
     * @param player Player that the NPCs will look at.
     */
    private void moveNearNpcHeads(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        double lookDistance = Math.pow(ConfigManager.getNpcLookDistance(), 2);
        World playerWorld = player.getWorld();
        Location playerLocation = player.getLocation();
        for (AbstractNPCShop shop : plugin.getNpcShops().values()) {
            EntityPlayer entityPlayer = shop.getEntityPlayer();
            Location npcLocation = entityPlayer.getBukkitEntity().getLocation();
            if (!playerWorld.equals(npcLocation.getWorld())) continue;
            if (playerLocation.distanceSquared(npcLocation) > lookDistance) continue;
            // Calculate the location the NPC should look to
            Location newNpcLocation = npcLocation.setDirection(playerLocation.clone().subtract(npcLocation).toVector());
            float yaw = newNpcLocation.getYaw();
            float pitch = newNpcLocation.getPitch();
            connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(entityPlayer.getId(),
                    (byte) ((yaw % 360.) * 256 / 360), (byte) ((pitch % 360.) * 256 / 360), false));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) ((yaw % 360) * 256 / 360)));
        }
    }

    @EventHandler
    public void onNpcRightClick(NPCRightClickEvent event) {
        EntityPlayer clickedEntityPlayer = event.getEntityPlayer();
        AbstractNPCShop npcShop = AbstractNPCShop.fromEntityPlayer(clickedEntityPlayer);
        if (npcShop == null) return;
        Player player = event.getPlayer();
        if (plugin.getNpcShopOperators().containsKey(player.getUniqueId())) {
            NPCShopOperator npcShopOperator = plugin.getNpcShopOperators().get(player.getUniqueId());
            NPCShopOperation operation = npcShopOperator.getOperation();
            if (operation == NPCShopOperation.CONFIGURE) {
                player.openInventory(npcShop.getOwnerGUI());
                return;
            }
            if (operation == NPCShopOperation.DELETE) {
                deleteNpcShop(player, npcShop);
                return;
            }
            if (operation == NPCShopOperation.LINK) {
                Chest chest = npcShopOperator.getLinkingChest();
                if (npcShop.isChestLinked(chest)) {
                    Messager.sendErrorMessage(player, "&cThis chest is already linked with this NPC.");
                    return;
                }
                npcShop.linkChest(npcShopOperator.getLinkingChest());
                Messager.sendMessage(player, "&aChest successfully linked.");
                SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 0.6F, 1.4F);
                return;
            }
            if (operation == NPCShopOperation.EQUIPMENT) {
                equipNpcArmor(player, npcShopOperator.getConfiguringSlot(), npcShop);
                SFXManager.playWorldSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 0.8F, 1F);
                return;
            }
            return;
        }
        player.openInventory(npcShop.getShopGUI());
    }

    /**
     * Attemps to delete the passed NPCShop, if the player hasn't been asked for confirmation ask for it, if it has then
     * directly delete the NPC.
     *
     * @param player  Player that is deleting the NPCShop.
     * @param npcShop NPCShop that is being deleted.
     */
    private void deleteNpcShop(Player player, AbstractNPCShop npcShop) {
        UUID playerUuid = player.getUniqueId();
        if (!deleteConfirmations.contains(playerUuid)) {
            deleteConfirmations.add(playerUuid);
            Bukkit.getScheduler().runTaskLater(plugin, () -> deleteConfirmations.remove(playerUuid), 100L);
            Messager.sendMessage(player, "&eRight click again to confirm the deletion of this NPC.");
            SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.6F, 1.4F);
            return;
        }
        deleteConfirmations.remove(playerUuid);
        npcShop.deleteNpcShop();
        SFXManager.playPlayerSound(player, Sound.BLOCK_GRAVEL_PLACE, 0.6F, 1.9F);
    }

    /**
     * Equips the passed entity player with the ItemStack the passed player has in hand to the passed EquipmentSlot.
     *
     * @param player          Player that is equipping the NPC with armor.
     * @param equipmentSlot   EquipmentSlot where the armor will go.
     * @param abstractNpcShop NPCShop that will be equipped with armor.
     */
    private void equipNpcArmor(Player player, EquipmentSlot equipmentSlot, AbstractNPCShop abstractNpcShop) {
        // Parsing arguments to create EntityEquipmentPacket
        ItemStack equippedItem = player.getInventory().getItemInMainHand();
        List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> newEquipment = new ArrayList<>();
        EnumItemSlot nmsSlot = parseEquipmentSlot(equipmentSlot);
        net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(equippedItem);
        Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack> equipment = new Pair<>(nmsSlot, nmsItemStack);
        newEquipment.add(equipment);
        // Sending packet to all online players and modifying NPC equipment
        EntityPlayer entityPlayer = abstractNpcShop.getEntityPlayer();
        PacketPlayOutEntityEquipment equipmentPacket = new PacketPlayOutEntityEquipment(entityPlayer.getId(), newEquipment);
        World npcWorld = player.getWorld();
        for (Player online : player.getWorld().getPlayers()) {
            if (!npcWorld.equals(online.getWorld())) continue;

            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
            playerConnection.sendPacket(equipmentPacket);
        }
        abstractNpcShop.addEquipment(equipment);
    }

    /**
     * Parses the passed EquipmentSlot to its EnumItemSlot version.
     *
     * @param equipmentSlot EquipmentSlot to parse.
     * @return EnumItemSlot that corresponds the passed EquipmentSlot.
     */
    private EnumItemSlot parseEquipmentSlot(EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.HAND) {
            return EnumItemSlot.MAINHAND;
        } else if (equipmentSlot == EquipmentSlot.OFF_HAND) {
            return EnumItemSlot.OFFHAND;
        } else {
            return EnumItemSlot.valueOf(equipmentSlot.name());
        }
    }
}
