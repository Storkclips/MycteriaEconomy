package me.wmorales01.mycteriaeconomy.files;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.EconomyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class EconomyPlayerManager {
    private final MycteriaEconomy plugin;

    public EconomyPlayerManager(MycteriaEconomy plugin) {
        this.plugin = plugin;
    }

    public void saveEconomyPlayer(EconomyPlayer economyPlayer) {
        EconomyPlayerFile economyPlayerFile = new EconomyPlayerFile(plugin, economyPlayer);
        FileConfiguration data = economyPlayerFile.getData();
        String path = "economy-players." + economyPlayer.getPlayer().getUniqueId() + ".";
        data.set(path + "balance", economyPlayer.getBankBalance());
        economyPlayerFile.saveData();
    }

    public void saveOnlineEconomyPlayers() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            EconomyPlayer.fromPlayer(online).unregisterEconomyPlayer();
        }
    }

    public EconomyPlayer loadEconomyPlayer(Player player) {
        EconomyPlayerFile economyPlayerFile = new EconomyPlayerFile(plugin, player);
        FileConfiguration data = economyPlayerFile.getData();
        if (data.getKeys(true).size() == 0) {
            return new EconomyPlayer(player);
        }
        double bankBalance = data.getDouble("balance");
        return new EconomyPlayer(player, bankBalance);
    }

    public void loadOnlineEconomyPlayers() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            loadEconomyPlayer(online).registerEconomyPlayer();
        }
    }
}
