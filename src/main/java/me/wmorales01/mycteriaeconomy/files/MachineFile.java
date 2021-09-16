package me.wmorales01.mycteriaeconomy.files;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.AbstractMachine;
import me.wmorales01.mycteriaeconomy.models.ShopType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MachineFile {
    private File dataFile;
    private FileConfiguration dataYML;

    public MachineFile(MycteriaEconomy plugin, AbstractMachine machine) {
        createDataFile(plugin, machine.getMachineUuid().toString(), machine.getShopType());
    }

    public MachineFile(MycteriaEconomy plugin, String machineUuidString, ShopType shopType) {
        createDataFile(plugin, machineUuidString, shopType);
    }

    private void createDataFile(MycteriaEconomy plugin, String machineUuidString, ShopType shopType) {
        this.dataFile = new File(plugin.getDataFolder() + "/machines/" + shopType.name() + "/" +
                machineUuidString + ".yml");
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

    public File getDataFile() {
        return dataFile;
    }
}
