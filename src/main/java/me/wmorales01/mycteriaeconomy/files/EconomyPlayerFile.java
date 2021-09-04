package me.wmorales01.mycteriaeconomy.files;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.EconomyPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class EconomyPlayerFile {
    private File dataFile;
    private FileConfiguration dataYML;

    public EconomyPlayerFile(MycteriaEconomy plugin, EconomyPlayer economyPlayer) {
        String playerUuidString = economyPlayer.getPlayer().getUniqueId().toString();
        createDataFile(plugin, playerUuidString);
    }

    public EconomyPlayerFile(MycteriaEconomy plugin, Player player) {
        String playerUuidString = player.getUniqueId().toString();
        createDataFile(plugin, playerUuidString);
    }

    private void createDataFile(MycteriaEconomy plugin, String playerUuidString) {
        this.dataFile = new File(plugin.getDataFolder() + "/economy_players/" + playerUuidString + ".yml");
        if (!dataFile.exists()) {
            if (!dataFile.getParentFile().exists()) {
                dataFile.getParentFile().mkdirs();
            }
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.dataYML = YamlConfiguration.loadConfiguration(dataFile);
    }

    public FileConfiguration getData() {
        return dataYML;
    }

    public void saveData() {
        try {
            dataYML.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadData() {
        dataYML.setDefaults(YamlConfiguration.loadConfiguration(dataFile));
    }
}
