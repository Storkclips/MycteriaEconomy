package me.spiderdeluxe.mycteriaeconomy.files;

import me.spiderdeluxe.mycteriaeconomy.MycteriaEconomyPlugin;
import me.spiderdeluxe.mycteriaeconomy.models.EconomyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class EconomyPlayerManager {
    private final MycteriaEconomyPlugin plugin;

    public EconomyPlayerManager(final MycteriaEconomyPlugin plugin) {
        this.plugin = plugin;
    }

    public void saveEconomyPlayer(final EconomyPlayer economyPlayer) {
        final EconomyPlayerFile economyPlayerFile = new EconomyPlayerFile(plugin, economyPlayer);
        final FileConfiguration data = economyPlayerFile.getData();
        data.set("balance", economyPlayer.getBankBalance());
        economyPlayerFile.saveData();
    }

    public void saveOnlineEconomyPlayers() {
        for (final Player online : Bukkit.getOnlinePlayers()) {
            EconomyPlayer.fromPlayer(online).unregisterEconomyPlayer();
        }
    }

    public EconomyPlayer loadEconomyPlayer(final Player player) {
        final EconomyPlayerFile economyPlayerFile = new EconomyPlayerFile(plugin, player);
        final FileConfiguration data = economyPlayerFile.getData();
        if (data.getKeys(true).size() == 0) {
            return new EconomyPlayer(player);
        }
        final double bankBalance = data.getDouble("balance");
        return new EconomyPlayer(player, bankBalance);
    }

    public void loadOnlineEconomyPlayers() {
        for (final Player online : Bukkit.getOnlinePlayers()) {
            loadEconomyPlayer(online).registerEconomyPlayer();
        }
    }
}
