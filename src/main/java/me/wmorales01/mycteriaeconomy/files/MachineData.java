package me.wmorales01.mycteriaeconomy.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;

public class MachineData {
	private MycteriaEconomy plugin;
	private FileConfiguration dataConfig = null;
	private File configFile = null;

	public MachineData(MycteriaEconomy instance) {
		this.plugin = instance;
		saveDefaultConfig();
	}

	public void reloadConfig() {
		if (configFile == null) {
			configFile = new File(plugin.getDataFolder(), "machine_data.yml");
		}
		dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
		InputStream defaultStream = plugin.getResource("machine_data.yml");
		if (defaultStream != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
			dataConfig.setDefaults(defaultConfig);
		}
	}

	public FileConfiguration getConfig() {
		if (dataConfig == null) {
			reloadConfig();
		}
		return dataConfig;
	}

	public void saveConfig() {
		if (dataConfig == null || configFile == null) {
			return;
		}
		try {
			getConfig().save(configFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Couldn't save information into " + configFile, e);
		}
	}

	public void saveDefaultConfig() {
		if (configFile == null) {
			configFile = new File(plugin.getDataFolder(), "machine_data.yml");
		}
		if (!configFile.exists()) {
			plugin.saveResource("machine_data.yml", false);
		}
	}
}
