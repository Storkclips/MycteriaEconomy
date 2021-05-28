package me.wmorales01.mycteriaeconomy.files;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MachineManager {
    private MycteriaEconomy plugin;
    private FileConfiguration data;

    public MachineManager(MycteriaEconomy instance) {
        plugin = instance;
    }

    public void saveAllMachines() {
        saveATMs();
        saveMachines(plugin.getVendingMachines(), "vending-machines.");
        saveMachines(plugin.getTradingMachines(), "trading-machines.");

    }

    public void loadAllMachines() {
        loadATMs();
        loadMachines("vending-machines.");
        loadMachines("trading-machines.");
    }

    public void saveATMs() {
        data = plugin.getMachineData();
        if (plugin.getATMs().isEmpty())
            return;

        int var = 1;
        for (ATM atm : plugin.getATMs()) {
            Location atmLocation = atm.getLocation();

            int x = (int) atmLocation.getX();
            int y = (int) atmLocation.getY();
            int z = (int) atmLocation.getZ();
            String world = atmLocation.getWorld().getName();

            data.set("atms." + var + ".x", x);
            data.set("atms." + var + ".y", y);
            data.set("atms." + var + ".z", z);
            data.set("atms." + var + ".world", world);
            var++;
        }

        plugin.saveMachineData();
    }

    public void loadATMs() {
        data = plugin.getMachineData();
        ConfigurationSection atmSection = data.getConfigurationSection("atms");

        if (atmSection == null)
            return;

        atmSection.getKeys(false).forEach(atmKey -> {
            String path = "atms." + atmKey;

            int x = data.getInt(path + ".x");
            int y = data.getInt(path + ".y");
            int z = data.getInt(path + ".z");
            World world = Bukkit.getWorld(data.getString(path + ".world"));

            Location atmLocation = new Location(world, x, y, z);
            ATM atm = new ATM(atmLocation);

            plugin.getATMs().add(atm);
        });
        data.set("atms", null);
        plugin.saveMachineData();
    }

    public void saveMachines(List<? extends Machine> machines, String path) {
        data = plugin.getMachineData();
        if (path.contains("vending-machines") && plugin.getVendingMachines().isEmpty())
            return;
        else if (path.contains("trading-machines") && plugin.getTradingMachines().isEmpty())
            return;

        int var = 1;
        for (Machine machine : machines) {
            String ownerUuid = machine.getOwnerUUID().toString();
            data.set(path + var + ".owner", ownerUuid);

            if (machine instanceof VendingMachine) {
                double profit = ((VendingMachine) machine).getProfit();
                data.set(path + var + ".profit", profit);

            } else if (machine instanceof TradingMachine) {
                double machineBalance = ((TradingMachine) machine).getMachineBalance();
                data.set(path + var + ".machine-balance", machineBalance);
            }
            Location machineLocation = machine.getLocation();
            int x = machineLocation.getBlockX();
            int y = machineLocation.getBlockY();
            int z = machineLocation.getBlockZ();
            String world = machineLocation.getWorld().getName();

            data.set(path + var + ".location.x", x);
            data.set(path + var + ".location.y", y);
            data.set(path + var + ".location.z", z);
            data.set(path + var + ".location.world", world);

            List<Location> chestLocations = machine.getChestLocations();
            int chestVar = 1;
            for (Location chestLocation : chestLocations) {
                x = chestLocation.getBlockX();
                y = chestLocation.getBlockY();
                z = chestLocation.getBlockZ();
                world = chestLocation.getWorld().getName();

                data.set(path + var + ".chest-locations" + "." + chestVar + ".x", x);
                data.set(path + var + ".chest-locations" + "." + chestVar + ".y", y);
                data.set(path + var + ".chest-locations" + "." + chestVar + ".z", z);
                data.set(path + var + ".chest-locations" + "." + chestVar + ".world", world);
                chestVar++;
            }
            List<MachineItem> stock = machine.getStock();
            int varItem = 1;
            for (MachineItem machineItem : stock) {
                ItemStack item = machineItem.getItemStack();
                double sellPrice = machineItem.getPrice();
                int sellAmount = machineItem.getSellAmount();
                int stockAmount = machineItem.getStockAmount();

                data.set(path + var + ".stock." + varItem + ".item", item);
                data.set(path + var + ".stock." + varItem + ".sell-price", sellPrice);
                data.set(path + var + ".stock." + varItem + ".sell-amount", sellAmount);
                data.set(path + var + ".stock." + varItem + ".stock-amount", stockAmount);
                varItem++;
            }
            var++;
        }

        plugin.saveMachineData();
    }

    public void loadMachines(String path) {
        data = plugin.getMachineData();
        if (path == null) {
            loadMachines("vending-machines.");
            loadMachines("trading-machines.");
            return;
        }
        ConfigurationSection dataSection = data.getConfigurationSection(path.replace(".", ""));
        if (dataSection != null)
            dataSection.getKeys(false).forEach(machineKey -> {
                String dataPath = path + machineKey;

                UUID uuid = UUID.fromString(data.getString(dataPath + ".owner"));

                int x = data.getInt(dataPath + ".location.x");
                int y = data.getInt(dataPath + ".location.y");
                int z = data.getInt(dataPath + ".location.z");
                World world = Bukkit.getWorld(data.getString(dataPath + ".location.world"));
                Location machineLocation = new Location(world, x, y, z);

                ConfigurationSection chestSection = dataSection.getConfigurationSection(machineKey + ".chest-locations");
                List<Location> chestLocations = new ArrayList<>();
                if (chestSection != null) {
                    chestSection.getKeys(false).forEach(location -> {
                        int chestX = chestSection.getInt(location + ".x");
                        int chestY = chestSection.getInt(location + ".y");
                        int chestZ = chestSection.getInt(location + ".z");
                        World chestWorld = Bukkit.getWorld(chestSection.getString(location + ".world"));
                        chestLocations.add(new Location(chestWorld, chestX, chestY, chestZ));
                    });
                }
                List<MachineItem> stock = loadMachineStock(dataPath);

                if (path.contains("vending-machine")) {
                    double profit = data.getDouble(dataPath + ".profit");
                    VendingMachine vendingMachine = new VendingMachine(uuid, machineLocation, stock, chestLocations,
                            profit);
                    vendingMachine.registerMachine();

                } else if (path.contains("trading-machine")) {
                    double machineBalance = data.getDouble(dataPath + ".machine-balance");
                    TradingMachine tradingMachine = new TradingMachine(uuid, machineLocation, stock, chestLocations,
                            machineBalance);
                    tradingMachine.registerMachine();
                }
            });

        data.set(path.replace(".", ""), null);
        plugin.saveMachineData();
    }

    private List<MachineItem> loadMachineStock(String dataPath) {
        data = plugin.getMachineData();
        ConfigurationSection stockSection = data.getConfigurationSection(dataPath + ".stock");
        List<MachineItem> stock = new ArrayList<MachineItem>();
        if (stockSection == null)
            return stock;

        stockSection.getKeys(false).forEach(itemKey -> {
            String path = dataPath + ".stock." + itemKey;
            ItemStack item = data.getItemStack(path + ".item");
            double price = data.getDouble(path + ".sell-price");
            int sellAmount = data.getInt(path + ".sell-amount");
            int stockAmount = data.getInt(path + ".stock-amount");

            MachineItem machineItem = new MachineItem(item, sellAmount, price, stockAmount);
            stock.add(machineItem);
        });
        return stock;
    }
}
