package me.wmorales01.mycteriaeconomy.files;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.*;
import me.wmorales01.mycteriaeconomy.util.LogUtil;
import me.wmorales01.mycteriaeconomy.util.YAMLUtil;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class MachineManager {
    private final MycteriaEconomy plugin;

    public MachineManager(MycteriaEconomy plugin) {
        this.plugin = plugin;
    }

    /**
     * Saves the passed machine into its respective .yml file.
     *
     * @param machine machine that will be saved in a .yml file
     */
    public void saveMachine(AbstractMachine machine) {
        MachineFile machineFile = new MachineFile(plugin, machine);
        FileConfiguration machineData = machineFile.getData();
        machineData.set("owner-uuid", machine.getOwnerUuid().toString());
        YAMLUtil.saveBlockLocationToYaml(machine.getMachineLocation(), machineData, "location");
        YAMLUtil.saveLinkedChests(machine.getLinkedChests(), machineData);
        YAMLUtil.saveMachineItems(machine.getShopItems(), machineData);
        if (machine instanceof VendingMachine) {
            VendingMachine vendingMachine = (VendingMachine) machine;
            machineData.set("profit", vendingMachine.getProfit());
        } else if (machine instanceof TradingMachine) {
            TradingMachine tradingMachine = (TradingMachine) machine;
            machineData.set("balance", tradingMachine.getBalance());
        }
        machineFile.saveData();
    }

    /**
     * Saves all the currently loaded machines to their corresponding .yml files.
     */
    public void saveMachines() {
        for (AbstractMachine machine : plugin.getMachines().values()) {
            saveMachine(machine);
        }
        plugin.getMachines().clear();
    }

    /**
     * Loads the machine that corresponds with the passed UUID, it will handle the data load differently depending on
     * the machine type.
     *
     * @param machineUuidString UUID of the machine as a String
     * @param shopType          the MachineType of the machine that will be loaded
     * @return the Machine instance corresponding the passed UUID
     */
    public AbstractMachine loadMachine(String machineUuidString, ShopType shopType) {
        MachineFile machineFile = new MachineFile(plugin, machineUuidString, shopType);
        FileConfiguration machineData = machineFile.getData();
        String ownerUuidString = machineData.getString("owner-uuid");
        Location machineLocation = YAMLUtil.loadLocationFromYaml(machineData, "location");
        if (machineLocation == null) {
            LogUtil.sendWarnLog("There was an error loading Machine with UUID '" + machineUuidString + "'.");
            return null;
        }
        List<Chest> linkedChests = YAMLUtil.loadLinkedChests(machineData);
        List<ShopItem> shopItems = YAMLUtil.loadMachineItems(machineData);
        // Parsing String data
        UUID machineUuid = UUID.fromString(machineUuidString);
        if (ownerUuidString == null) {
            LogUtil.sendWarnLog("Invalid owner UUID provided in machine file " + machineData.getName() + ".");
            return null;
        }
        UUID ownerUuid = UUID.fromString(ownerUuidString);
        if (shopType == ShopType.VENDING) {
            double profit = machineData.getDouble("profit");
            return new VendingMachine(linkedChests, shopItems, machineUuid, ownerUuid, machineLocation, profit);
        } else {
            double balance = machineData.getDouble("balance");
            return new TradingMachine(linkedChests, shopItems, machineUuid, ownerUuid, machineLocation, balance);
        }
    }

    /**
     * Loads all the machines existing on the machines directory on the plugin's folder.
     */
    public void loadMachines() {
        File machinesDirectory = new File(plugin.getDataFolder() + "/machines/");
        for (ShopType shopType : ShopType.values()) {
            File machinesSubdirectory = new File(machinesDirectory, "/" + shopType.name() + "/");
            if (machinesSubdirectory.listFiles() == null) continue;
            for (File machineFile : machinesSubdirectory.listFiles()) {
                // Getting the uuid from the .yml file name.
                String machineUuidString = machineFile.getName().split("\\.")[0];
                AbstractMachine machine = loadMachine(machineUuidString, shopType);
                if (machine == null) continue;

                machine.registerMachine();
            }
        }
    }

    /**
     * Deletes the passed machine's .yml file.
     *
     * @param machine Abstract Machine to be deleted.
     */
    public void deleteMachine(AbstractMachine machine) {
        MachineFile machineFile = new MachineFile(plugin, machine);
        File dataFile = machineFile.getDataFile();
        try {
            dataFile.delete();
        } catch (Exception e) {
            LogUtil.sendWarnLog("There was an error deleting Machine with UUID '" + machine.getMachineUuid().toString() +
                    "'.");
            e.printStackTrace();
        }
    }
}
