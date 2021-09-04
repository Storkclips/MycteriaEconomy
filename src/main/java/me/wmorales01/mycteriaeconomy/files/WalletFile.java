package me.wmorales01.mycteriaeconomy.files;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.Wallet;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class WalletFile {
    private File dataFile;
    private FileConfiguration dataYML;

    public WalletFile(MycteriaEconomy plugin, Wallet wallet) {
        createDataFile(plugin, wallet.getUuid().toString());
    }

    public WalletFile(MycteriaEconomy plugin, UUID walletUuid) {
        createDataFile(plugin, walletUuid.toString());
    }

    private void createDataFile(MycteriaEconomy plugin, String walletUuidString) {
        this.dataFile = new File(plugin.getDataFolder() + "/wallets/" + walletUuidString + ".yml");
        if (!dataFile.getParentFile().exists()) {
            dataFile.getParentFile().mkdirs();
        }
        if (!dataFile.exists()) {
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
