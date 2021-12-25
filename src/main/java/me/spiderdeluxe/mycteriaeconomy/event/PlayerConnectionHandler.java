package me.spiderdeluxe.mycteriaeconomy.event;

import me.spiderdeluxe.mycteriaeconomy.MycteriaEconomyPlugin;
import me.spiderdeluxe.mycteriaeconomy.files.EconomyPlayerManager;
import me.spiderdeluxe.mycteriaeconomy.models.EconomyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionHandler implements Listener {
    private final MycteriaEconomyPlugin plugin;
    private final EconomyPlayerManager economyPlayerManager;

    public PlayerConnectionHandler(final MycteriaEconomyPlugin plugin) {
        this.plugin = plugin;
        this.economyPlayerManager = plugin.getEconomyPlayerManager();
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        economyPlayerManager.loadEconomyPlayer(player).registerEconomyPlayer();
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        EconomyPlayer.fromPlayer(player).unregisterEconomyPlayer();
    }
}
