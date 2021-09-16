package me.wmorales01.mycteriaeconomy.util;

import me.wmorales01.mycteriaeconomy.models.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YAMLUtil {

    // Saves the passed Location to the passed data file directly tot he passed path
    // It uses exact locations, which means that X Y and Z coordinates will be floating numbers
    public static void saveLocationToYaml(Location location, FileConfiguration dataFile, String path) {
        dataFile.set(path + ".x", location.getX());
        dataFile.set(path + ".y", location.getY());
        dataFile.set(path + ".z", location.getZ());
        dataFile.set(path + ".world", location.getWorld().getName());
    }

    // Saves the passed Location to the passed data file directly to the passed path
    // It uses block locations, which means that X Y and Z coordinates will be int numbers
    public static void saveBlockLocationToYaml(Location location, FileConfiguration dataFile, String path) {
        dataFile.set(path + ".x", location.getBlockX());
        dataFile.set(path + ".y", location.getBlockY());
        dataFile.set(path + ".z", location.getBlockZ());
        dataFile.set(path + ".world", location.getWorld().getName());
    }

    // Saves the passed location to the passed data file directly to the passed path
    // It uses entity locations, which means that X Y and Z coordinates will be floating numbers and also saves pitch and yaw
    public static void saveEntityLocationToYaml(Location location, FileConfiguration dataFile, String path) {
        dataFile.set(path + ".x", location.getX());
        dataFile.set(path + ".y", location.getY());
        dataFile.set(path + ".z", location.getZ());
        dataFile.set(path + ".pitch", location.getPitch());
        dataFile.set(path + ".yaw", location.getYaw());
        dataFile.set(path + ".world", location.getWorld().getName());
    }

    public static Location loadLocationFromYaml(FileConfiguration dataFile, String path) {
        int x = dataFile.getInt(path + ".x");
        int y = dataFile.getInt(path + ".y");
        int z = dataFile.getInt(path + ".z");
        float pitch = (float) dataFile.getDouble(path + ".pitch");
        float yaw = (float) dataFile.getDouble(path + ".yaw");
        String worldName = dataFile.getString(path + ".world");
        if (worldName == null) {
            LogUtil.sendWarnLog("Missing World data for location saved on '" + dataFile.getName() + "' in path '" +
                    path + "'.");
            return null;
        }
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            LogUtil.sendWarnLog("Unknown world '" + worldName + "' for location saved on '" + dataFile.getName() +
                    "' in path '" + path + "'.");
            return null;
        }
        Location location = new Location(world, x, y, z);
        location.setPitch(pitch);
        location.setYaw(yaw);
        return location;
    }

    /**
     * Saves the linked chests of the passed machine to the passed FileConfiguration.
     *
     * @param linkedChests Chests that will be saved into the YAML file.
     * @param dataFile     YAML file where the chest data will be saved.
     */
    public static void saveLinkedChests(List<Chest> linkedChests, FileConfiguration dataFile) {
        String path = "linked-chests";
        dataFile.set(path, null);
        for (int i = 0; i < linkedChests.size(); i++) {
            Chest linkedChest = linkedChests.get(i);
            if (linkedChest == null) continue;

            YAMLUtil.saveBlockLocationToYaml(linkedChest.getLocation(), dataFile, path + "." + i);
        }
    }

    /**
     * Reads the locations of the linked chests from the passed data file and finds them in their respective worlds
     * to check if they stil exist.
     *
     * @param dataFile file where the data will be extracted from.
     * @return a list of all the linked and existing chests.
     */
    public static List<Chest> loadLinkedChests(FileConfiguration dataFile) {
        List<Chest> linkedChests = new ArrayList<>();
        ConfigurationSection chestsSection = dataFile.getConfigurationSection("linked-chests");
        if (chestsSection == null) return linkedChests;
        for (String varKey : chestsSection.getKeys(false)) {
            Location chestLocation = YAMLUtil.loadLocationFromYaml(dataFile, "linked-chests." + varKey);
            BlockState blockState = chestLocation.getBlock().getState();
            if (!(blockState instanceof Chest)) continue;

            linkedChests.add((Chest) blockState);
        }
        return linkedChests;
    }

    /**
     * Saves all the Machine Items from the passed machine to the passed machine data file.
     *
     * @param shopItems Machine Items that will be saved.
     * @param fileData  FileConfiguration where Machine Items will be saved.
     */
    public static void saveMachineItems(List<ShopItem> shopItems, FileConfiguration fileData) {
        String path = "machine-items";
        fileData.set(path, null);
        for (ShopItem shopItem : shopItems) {
            String subPath = path + "." + shopItem.getUuid().toString() + ".";
            fileData.set(subPath + "item", shopItem.getItemStack());
            fileData.set(subPath + "sell-amount", shopItem.getSellAmount());
            fileData.set(subPath + "price", shopItem.getPrice());
        }
    }

    /**
     * Goes through all the saved machine items in the passed machine file and compiles them into a list.
     *
     * @param dataFile FileConfiguration where the Machine Items will be loaded from.
     * @return The list of saved MachineItems.
     */
    public static List<ShopItem> loadMachineItems(FileConfiguration dataFile) {
        List<ShopItem> shopItems = new ArrayList<>();
        String path = "machine-items";
        ConfigurationSection machineItemSection = dataFile.getConfigurationSection(path);
        if (machineItemSection == null) return shopItems;
        for (String uuidKey : machineItemSection.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidKey);
            ItemStack item = machineItemSection.getItemStack(uuidKey + ".item");
            int sellAmount = machineItemSection.getInt(uuidKey + ".sell-amount");
            double price = machineItemSection.getDouble(uuidKey + ".price");
            shopItems.add(new ShopItem(uuid, item, price, sellAmount));
        }
        return shopItems;
    }
}
