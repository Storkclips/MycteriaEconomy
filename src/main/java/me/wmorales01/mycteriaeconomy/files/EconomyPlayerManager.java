package me.wmorales01.mycteriaeconomy.files;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.EconomyPlayer;

public class EconomyPlayerManager {
	private MycteriaEconomy plugin;
	FileConfiguration data;
	
	public EconomyPlayerManager(MycteriaEconomy instance) {
		plugin = instance;
	}
	
	public void saveEconomyPlayers() {
		data = plugin.getEcoPlayerData();
		if (plugin.getEconomyPlayers().isEmpty())
			return;
		
		for (EconomyPlayer ecoPlayer : plugin.getEconomyPlayers()) {
			String uuid = ecoPlayer.getUuid().toString();
			double balance = ecoPlayer.getBankBalance();
			
			data.set("economy-players." + uuid + ".balance", balance);
		}
		plugin.saveEcoPlayerData();
	}
	
	public void loadEconomyPlayers() {
		data = plugin.getEcoPlayerData();
		ConfigurationSection playerSection = data.getConfigurationSection("economy-players");
		if (playerSection == null)
			return;
		
		playerSection.getKeys(false).forEach(stringUuid -> {
			UUID uuid = UUID.fromString(stringUuid);
			double balance = data.getDouble("economy-players." + stringUuid + ".balance");
			
			EconomyPlayer ecoPlayer = new EconomyPlayer(uuid, balance);
			plugin.addEconomyPlayers(ecoPlayer);
		});
	}
}
