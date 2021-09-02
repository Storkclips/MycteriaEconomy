package me.wmorales01.mycteriaeconomy.files;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class ATMsFile {
    private final MycteriaEconomy plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public ATMsFile(MycteriaEconomy plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
    }

    public void reloadData() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "atms.yml");
        }
        dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defaultStream = plugin.getResource("atms.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getData() {
        if (dataConfig == null) {
            reloadData();
        }
        return dataConfig;
    }

    public void saveData() {
        if (dataConfig == null || configFile == null) return;
        try {
            getData().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save information into " + configFile, e);
        }
    }

    private void saveDefaultConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "atms.yml");
        }
        if (!configFile.exists()) {
            plugin.saveResource("atms.yml", false);
        }
    }
}

