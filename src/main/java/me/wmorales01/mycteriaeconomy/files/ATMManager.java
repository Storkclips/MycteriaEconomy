package me.wmorales01.mycteriaeconomy.files;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.ATM;
import me.wmorales01.mycteriaeconomy.util.YAMLUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class ATMManager {
    private final ATMsFile atmsFile;

    public ATMManager(MycteriaEconomy plugin) {
        this.atmsFile = new ATMsFile(plugin);
    }

    public void saveATM(ATM atm) {
        FileConfiguration atmData = getATMData();
        Location atmLocation = atm.getLocation();
        String path = atm.getUuid().toString();
        YAMLUtil.saveBlockLocationToYaml(atmLocation, atmData, path);
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
            Location atmLocation = YAMLUtil.loadLocationFromYaml(data, uuidKey);
            if (atmLocation == null) continue;

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
