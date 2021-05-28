package me.wmorales01.mycteriaeconomy.models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import net.minecraft.server.v1_16_R3.*;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import java.util.List;
import java.util.UUID;

public class NPCManager {
    private MycteriaEconomy plugin;

    public NPCManager(MycteriaEconomy plugin) {
        this.plugin = plugin;
    }

    public void createNPC(Player creator, String skinName) {
        // Creating NPC
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        Location creatorLocation = creator.getLocation();
        WorldServer world = ((CraftWorld) Bukkit.getWorld(creatorLocation.getWorld().getName())).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
        EntityPlayer npc = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));

        // Hiding Name Tag
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        Team npcs = null;
        if (board.getTeam("npcs") == null)
            npcs = board.registerNewTeam("npcs");

        npcs = board.getTeam("npcs");
        npcs.addEntry(npc.getName());
        npcs.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
        npcs.addEntry(npc.getName());

        // Setting properties and location
        String[] properties = getSkin(creator, skinName);
        if (properties != null)
            gameProfile.getProperties().put("textures", new Property("textures", properties[0], properties[1]));
        npc.setLocation(creatorLocation.getX(), creatorLocation.getY(), creatorLocation.getZ(),
                creatorLocation.getYaw(), creatorLocation.getPitch());

        NPCShop shop = new NPCShop(npc);
        addNpcPacket(npc);
        plugin.getNpcs().add(shop);
        plugin.getNpcDataManager().saveNPC(shop);
    }

    public void deleteNPC(NPCShop shop, boolean deleteData) {
        EntityPlayer npc = shop.getNpc();
        for (Player online : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) online).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
        }
        if (deleteData) {
            plugin.getNpcDataManager().deleteNPC(shop);
            plugin.getNpcs().remove(shop);
        }
    }

    public void loadNpc(Location location, GameProfile profile, List<Location> chestLocations,
                        List<MachineItem> machineItems) {
        // Creating NPC
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile gameProfile = profile;
        EntityPlayer npc = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));

        // Setting Position and Properties
        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        addNpcPacket(npc);
        plugin.getNpcs().add(new NPCShop(npc, chestLocations, machineItems));
    }

    private void addNpcPacket(EntityPlayer npc) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld() != npc.getBukkitEntity().getWorld())
                continue;

            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            sendPackets(connection, npc);
        }
    }

    public void addJoinPacket(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        for (NPCShop npc : plugin.getNpcs()) {
            if (!player.getWorld().equals(npc.getNpc().getBukkitEntity().getWorld()))
                continue;

            sendPackets(connection, npc.getNpc());
        }
    }

    private void sendPackets(PlayerConnection connection, EntityPlayer npc) {
        connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, npc)),
                100L);
    }

    private String[] getSkin(Player creator, String skinName) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + skinName + "");
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL(
                    "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());
            JsonObject properties = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray()
                    .get(0).getAsJsonObject();

            String texture = properties.get("value").getAsString();
            String signature = properties.get("signature").getAsString();
            return new String[]{texture, signature};
        } catch (Exception e) {
            creator.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', "&cThe skin does not exist or is not available"));
            return null;
        }
    }
}
