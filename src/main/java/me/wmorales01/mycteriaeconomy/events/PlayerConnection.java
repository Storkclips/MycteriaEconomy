package me.wmorales01.mycteriaeconomy.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.NPCManager;
import me.wmorales01.mycteriaeconomy.models.PacketManager;

public class PlayerConnection implements Listener {
	private MycteriaEconomy plugin;
	private NPCManager npcManager;
	private PacketManager packetManager;

	public PlayerConnection(MycteriaEconomy plugin) {
		this.plugin = plugin;
		this.npcManager = new NPCManager(plugin);
		this.packetManager = new PacketManager(plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		npcManager.addJoinPacket(event.getPlayer());
		packetManager.injectPacket(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		packetManager.uninjectPacket(event.getPlayer());
	}

	@EventHandler
	public void onPlayerWorldChange(PlayerTeleportEvent event) {
		if (event.getFrom().getWorld().equals(event.getTo().getWorld()))
			return;

		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			npcManager.addJoinPacket(event.getPlayer());
		}, 1);
	}
}
