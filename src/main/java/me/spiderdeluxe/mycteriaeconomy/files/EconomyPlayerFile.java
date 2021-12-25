package me.spiderdeluxe.mycteriaeconomy.files;

import me.spiderdeluxe.mycteriaeconomy.MycteriaEconomyPlugin;
import me.spiderdeluxe.mycteriaeconomy.models.EconomyPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class EconomyPlayerFile {
    private File dataFile;
    private FileConfiguration dataYML;

    public EconomyPlayerFile(final MycteriaEconomyPlugin plugin, final EconomyPlayer economyPlayer) {
        final String playerUuidString = economyPlayer.getPlayer().getUniqueId().toString();
        createDataFile(plugin, playerUuidString);
    }

    public EconomyPlayerFile(final MycteriaEconomyPlugin plugin, final Player player) {
        final String playerUuidString = player.getUniqueId().toString();
        createDataFile(plugin, playerUuidString);
    }

    private void createDataFile(final MycteriaEconomyPlugin plugin, final String playerUuidString) {
        this.dataFile = new File(plugin.getDataFolder() + "/economy_players/" + playerUuidString + ".yml");
        if (!dataFile.exists()) {
            if (!dataFile.getParentFile().exists()) {
                dataFile.getParentFile().mkdirs();
            }
            try {
                dataFile.createNewFile();
            } catch (final IOException e) {
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
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadData() {
        dataYML.setDefaults(YamlConfiguration.loadConfiguration(dataFile));
    }
}
