//package me.wmorales01.mycteriaeconomy.files;
//
//import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
//import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.configuration.file.YamlConfiguration;
//
//import java.io.File;
//import java.io.IOException;
//
//public class MachineDataFile {
//    private File dataFile;
//    private FileConfiguration dataYML;
//
//    public MachineDataFile(MycteriaEconomy plugin) {
//        createDataFile(plugin);
//    }
//
//    public MachineDataFile(MycteriaEconomy plugin, String ownerUUID, String merchantName) {
//        createDataFile(plugin, ownerUUID, merchantName);
//    }
//
//    private void createDataFile(MoleArtis plugin, String ownerUUID, String merchantName) {
//        this.dataFile = new File(plugin.getDataFolder() + "/trading_posts/" + ownerUUID + "/" +
//                merchantName + ".yml");
//        if (!dataFile.exists()) {
//            try {
//                dataFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        this.dataYML = YamlConfiguration.loadConfiguration(dataFile);
//    }
//
//    public FileConfiguration getData() {
//        return dataYML;
//    }
//
//    public void saveData() {
//        try {
//            dataYML.save(dataFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void reloadData() {
//        dataYML.setDefaults(YamlConfiguration.loadConfiguration(dataFile));
//    }
//}
