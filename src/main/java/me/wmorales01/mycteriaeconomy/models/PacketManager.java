package me.wmorales01.mycteriaeconomy.models;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.customevents.RightClickNPCEvent;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayInUseEntity;

public class PacketManager {
	private MycteriaEconomy plugin;
	
	public PacketManager(MycteriaEconomy instance) {
		this.plugin = instance;
	}
	
	Channel channel;
	public static Map<UUID, Channel> channels = new HashMap<UUID, Channel>();

	public void injectPacket(Player player) {
		CraftPlayer craftPlayer = (CraftPlayer) player;
		channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
		channels.put(player.getUniqueId(), channel);

		if (channel.pipeline().get("PacketInjector") != null)
			return;

		channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<PacketPlayInUseEntity>() {

			@Override
			protected void decode(ChannelHandlerContext channel, PacketPlayInUseEntity packet, List<Object> arg)
					throws Exception {
				arg.add(packet);
				readPacket(player, packet);
			}
		});
	}

	public void uninjectPacket(Player player) {
		if (!channels.containsKey(player.getUniqueId()))
			return;

		channel = channels.get(player.getUniqueId());

		if (channel.pipeline().get("PacketInjector") != null)
			channel.pipeline().remove("PacketInjector");
	}

	private void readPacket(Player player, Packet<?> packet) {
		if (!packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity"))
			return;
		if (getValue(packet, "action").toString().equalsIgnoreCase("ATTACK"))
			return;
		if (getValue(packet, "d").toString().equalsIgnoreCase("OFF_HAND"))
			return;
		if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT_AT"))
			return;

		if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT")) {

			int id = (int) getValue(packet, "a");

			for (NPCShop npc : plugin.getNpcs()) {
				if (npc.getNpc().getId() != id)
					continue;

				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
						new Runnable() {

							@Override
							public void run() {
								Bukkit.getPluginManager().callEvent(new RightClickNPCEvent(player, npc.getNpc()));
							}
						}, 0);
			}
		}
	}

	private Object getValue(Object instance, String name) {
		Object result = null;

		try {
			Field field = instance.getClass().getDeclaredField(name);

			field.setAccessible(true);

			result = field.get(instance);

			field.setAccessible(false);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}