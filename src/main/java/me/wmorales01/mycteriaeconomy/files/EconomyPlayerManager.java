package me.wmorales01.mycteriaeconomy.files;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.EconomyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class EconomyPlayerManager {
    private final EconomyPlayersFile economyPlayersFile;

    public EconomyPlayerManager(MycteriaEconomy plugin) {
        this.economyPlayersFile = new EconomyPlayersFile(plugin);
    }

    public void saveEconomyPlayer(EconomyPlayer economyPlayer) {
        FileConfiguration data = getPlayersData();
        String path = "economy-players." + economyPlayer.getPlayer().getUniqueId() + ".";
        data.set(path + "balance", economyPlayer.getBankBalance());
        savePlayersData();
    }

    public void saveOnlineEconomyPlayers() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            EconomyPlayer.fromPlayer(online).unregisterEconomyPlayer();
        }
    }

    public EconomyPlayer loadEconomyPlayer(Player player) {
        ConfigurationSection playerSection = getPlayersData().getConfigurationSection("economy-players." +
                player.getUniqueId());
        if (playerSection == null) {
            return new EconomyPlayer(player);
        }
        double bankBalance = playerSection.getDouble("balance");
        return new EconomyPlayer(player, bankBalance);
    }

    public void loadOnlineEconomyPlayers() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            loadEconomyPlayer(online).registerEconomyPlayer();
        }
    }

    private FileConfiguration getPlayersData() {
        return economyPlayersFile.getData();
    }

    private void savePlayersData() {
        economyPlayersFile.saveData();
    }
}
