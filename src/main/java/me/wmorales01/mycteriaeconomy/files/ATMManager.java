package me.wmorales01.mycteriaeconomy.files;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.ATM;
import me.wmorales01.mycteriaeconomy.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class ATMManager {
    private final ATMsFile atmsFile;

    public ATMManager(MycteriaEconomy plugin) {
        this.atmsFile = new ATMsFile(plugin);
    }

    public void saveATM(ATM atm) {
        FileConfiguration data = getATMData();
        String path = atm.getUuid().toString() + ".";
        Location atmLocation = atm.getLocation();
        data.set(path + "x", atmLocation.getBlockX());
        data.set(path + "y", atmLocation.getBlockY());
        data.set(path + "z", atmLocation.getBlockZ());
        data.set(path + "world", atmLocation.getWorld().getName());
        saveATMData();
    }

    public void deleteATM(ATM atm) {
        getATMData().set(atm.getUuid().toString(), null);
        saveATMData();
    }

    public void loadATMs() {
        FileConfiguration data = getATMData();
        for (String uuidKey : data.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidKey);
            int x = data.getInt(uuidKey + ".x");
            int y = data.getInt(uuidKey + ".y");
            int z = data.getInt(uuidKey + ".z");
            String worldName = data.getString(uuidKey + ".world");
            if (worldName == null) {
                LogUtil.sendWarnLog("Missing world for ATM '" + uuidKey + "' in atms.yml.");
                continue;
            }
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                LogUtil.sendWarnLog("Unknown world for ATM '" + uuidKey + "' in atms.yml");
                continue;
            }
            Location atmLocation = new Location(world, x, y, z);
            new ATM(uuid, atmLocation).registerATM();
        }
    }

    private FileConfiguration getATMData() {
        return atmsFile.getData();
    }

    private void saveATMData() {
        atmsFile.saveData();
    }
}
