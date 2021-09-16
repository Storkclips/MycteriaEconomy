package me.wmorales01.mycteriaeconomy.models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.util.Messager;
import net.minecraft.server.v1_16_R3.*;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCManager {
    private final MycteriaEconomy plugin;

    public NPCManager(MycteriaEconomy plugin) {
        this.plugin = plugin;
    }

    public void createNPC(Player creator, String npcName, String skinName, ShopType shopType) {
        // Creating NPC
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        Location creatorLocation = creator.getLocation();
        WorldServer world = ((CraftWorld) Bukkit.getWorld(creatorLocation.getWorld().getName())).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "NPC");
        EntityPlayer entityPlayer = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
        // Hiding Name Tag
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        Team npcs;
        if (board.getTeam("npcs") == null) {
            npcs = board.registerNewTeam("npcs");
        } else {
            npcs = board.getTeam("npcs");
        }
        npcs.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
        npcs.addEntry(entityPlayer.getName());
        // Setting properties and location
        String[] properties = getSkin(creator, skinName);
        if (properties != null) {
            gameProfile.getProperties().put("textures", new Property("textures", properties[0], properties[1]));
        }
        entityPlayer.setLocation(creatorLocation.getX(), creatorLocation.getY(), creatorLocation.getZ(),
                creatorLocation.getYaw(), creatorLocation.getPitch());
        addNpcPacket(entityPlayer, new ArrayList<>());
        if (shopType == ShopType.VENDING) {
            new NPCVendor(entityPlayer, npcName).registerNpcShop();
        } else {
            new NPCTrader(entityPlayer, npcName).registerNpcShop();
        }
    }

    public void unloadNpc(AbstractNPCShop shop) {
        EntityPlayer entityPlayer = shop.getEntityPlayer();
        PacketPlayOutEntityDestroy deletePacket = new PacketPlayOutEntityDestroy(entityPlayer.getId());
        for (Player online : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) online).getHandle().playerConnection;
            connection.sendPacket(deletePacket);
        }
    }

    public EntityPlayer loadNpc(Location location, GameProfile gameProfile, List<Pair<EnumItemSlot, ItemStack>> equipment) {
        // Creating NPC
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        EntityPlayer entityPlayer = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
        // Setting Position and Properties
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
        addNpcPacket(entityPlayer, equipment);
        return entityPlayer;
    }

    private void addNpcPacket(EntityPlayer entityPlayer, List<Pair<EnumItemSlot, ItemStack>> equipment) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().equals(entityPlayer.getBukkitEntity().getWorld())) continue;

            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            sendPackets(connection, entityPlayer, equipment);
        }
    }

    public void addJoinPacket(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        World playerWorld = player.getWorld();
        for (AbstractNPCShop abstractNpcShop : plugin.getNpcShops().values()) {
            EntityPlayer entityPlayer = abstractNpcShop.getEntityPlayer();
            if (!playerWorld.equals(entityPlayer.getBukkitEntity().getWorld())) continue;

            sendPackets(connection, entityPlayer, abstractNpcShop.getEquipment());
        }
    }

    private void sendPackets(PlayerConnection connection, EntityPlayer entityPlayer, List<Pair<EnumItemSlot, ItemStack>> equipment) {
        connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) (entityPlayer.yaw * 256 / 360)));
        if (!equipment.isEmpty()) {
            connection.sendPacket(new PacketPlayOutEntityEquipment(entityPlayer.getId(), equipment));
        }
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer)),
                100L);
    }

    private String[] getSkin(Player player, String skinName) {
        try {
            URL mojangApiUrl = new URL("https://api.mojang.com/users/profiles/minecraft/" + skinName + "");
            InputStreamReader reader = new InputStreamReader(mojangApiUrl.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            URL mojangProfileUrl = new URL(
                    "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(mojangProfileUrl.openStream());
            JsonObject properties = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray()
                    .get(0).getAsJsonObject();

            String texture = properties.get("value").getAsString();
            String signature = properties.get("signature").getAsString();
            return new String[]{texture, signature};
        } catch (Exception e) {
            Messager.sendErrorMessage(player, "&cThe skin does not exist or is not available");
            return null;
        }
    }
}
