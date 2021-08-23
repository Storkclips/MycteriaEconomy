package me.wmorales01.mycteriaeconomy.listeners;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.files.EconomyPlayerManager;
import me.wmorales01.mycteriaeconomy.models.EconomyPlayer;
import me.wmorales01.mycteriaeconomy.models.NPCManager;
import me.wmorales01.mycteriaeconomy.models.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionHandler implements Listener {
	private final MycteriaEconomy plugin;
	private final EconomyPlayerManager economyPlayerManager;
	private final NPCManager npcManager;
	private final PacketManager packetManager;

	public PlayerConnectionHandler(MycteriaEconomy plugin) {
		this.plugin = plugin;
		this.economyPlayerManager = plugin.getEconomyPlayerManager();
		this.npcManager = new NPCManager(plugin);
		this.packetManager = new PacketManager(plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		economyPlayerManager.loadEconomyPlayer(player).registerEconomyPlayer();
		npcManager.addJoinPacket(player);
		packetManager.injectPacket(player);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		EconomyPlayer.fromPlayer(player).unregisterEconomyPlayer();
		packetManager.uninjectPacket(player);
	}

	@EventHandler
	public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (event.getFrom().equals(player.getWorld())) return;

		Bukkit.getScheduler().runTaskLater(plugin, () -> npcManager.addJoinPacket(player), 0L);
	}
}
