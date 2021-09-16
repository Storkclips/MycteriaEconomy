package me.wmorales01.mycteriaeconomy.files;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.*;
import me.wmorales01.mycteriaeconomy.util.LogUtil;
import me.wmorales01.mycteriaeconomy.util.YAMLUtil;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCDataManager {
    private final MycteriaEconomy plugin;
    private final NPCManager npcManager;

    public NPCDataManager(MycteriaEconomy plugin) {
        this.plugin = plugin;
        this.npcManager = new NPCManager(plugin);
    }

    /**
     * Saves the passed NPCShop to its corresponding .yml file.
     *
     * @param npcShop NPCShop that will be saved.
     */
    public void saveNpcShop(AbstractNPCShop npcShop) {
        NPCShopFile npcShopFile = new NPCShopFile(plugin, npcShop);
        FileConfiguration npcData = npcShopFile.getData();
        // Saving entity player data
        EntityPlayer entityPlayer = npcShop.getEntityPlayer();
        YAMLUtil.saveEntityLocationToYaml(entityPlayer.getBukkitEntity().getLocation(), npcData, "location");
        GameProfile profile = entityPlayer.getProfile();
        if (!profile.getProperties().get("textures").isEmpty()) {
            Property property = profile.getProperties().get("textures").iterator().next();
            npcData.set("texture", property.getValue());
            npcData.set("signature", property.getSignature());
        }
        saveNpcEquipment(npcShop, npcData);
        // Saving Hologram data
        Hologram hologram = npcShop.getHologram();
        YAMLUtil.saveEntityLocationToYaml(hologram.getLocation(), npcData, "hologram.location");
        npcData.set("hologram.text-lines", fetchHologramTextLines(hologram));
        // Saving Shop data
        YAMLUtil.saveLinkedChests(npcShop.getLinkedChests(), npcData);
        YAMLUtil.saveMachineItems(npcShop.getShopItems(), npcData);
        if (npcShop instanceof NPCVendor) {
            npcData.set("profit", ((NPCVendor) npcShop).getProfit());
        } else if (npcShop instanceof NPCTrader) {
            npcData.set("balance", ((NPCTrader) npcShop).getBalance());
        }
        npcShopFile.saveData();
    }

    /**
     * Saves the passed NPCShop's equipment to the passed data file.
     *
     * @param npcShop  NPCShop which equipment will be saved.
     * @param dataFile FileConfiguration where the passed entity's equipment will be saved.
     */
    private void saveNpcEquipment(AbstractNPCShop npcShop, FileConfiguration dataFile) {
        String path = "equipment.";
        for (Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack> pair : npcShop.getEquipment()) {
            String subPath = path + pair.getFirst().name() + ".";
            ItemStack equippedItem = CraftItemStack.asBukkitCopy(pair.getSecond());
            dataFile.set(subPath + "item", equippedItem);
        }
    }

    /**
     * Goes through all the HologramLines of the passed Hologram until it reaches the IndexOutOfBounds exception and saves
     * them into a List.
     *
     * @param hologram Hologram which lines will be fetched.
     * @return List of Text Lines of the passed Hologram.
     */
    private List<String> fetchHologramTextLines(Hologram hologram) {
        List<String> textLines = new ArrayList<>();
        int index = 0;
        while (true) {
            try {
                HologramLine hologramLine = hologram.getLine(index);
                if (!(hologramLine instanceof TextLine)) continue;

                textLines.add(((TextLine) hologramLine).getText());
            } catch (IndexOutOfBoundsException exception) {
                return textLines;
            }
            index++;
        }
    }

    /**
     * Saves all the currently loaded NPC Shops.
     */
    public void saveNpcShops() {
        NPCManager npcManager = new NPCManager(plugin);
        for (AbstractNPCShop abstractNpcShop : plugin.getNpcShops().values()) {
            saveNpcShop(abstractNpcShop);
            npcManager.unloadNpc(abstractNpcShop);
        }
        plugin.getNpcShops().clear();
    }

    /**
     * Loads the NPCShop corresponding to the passed UUID String.
     *
     * @param npcUuidString String with the UUID of the NPCShop that will be loaded.
     * @param shopType      Type of the NPCShop that will be loaded.
     * @return Instance of NPCShop that corresponds to the passed NPC UUID.
     */
    public AbstractNPCShop loadNpcShop(String npcUuidString, ShopType shopType) {
        NPCShopFile npcShopFile = new NPCShopFile(plugin, npcUuidString, shopType);
        FileConfiguration npcData = npcShopFile.getData();
        // Loading NPC data
        UUID npcUuid = UUID.fromString(npcUuidString);
        Location npcLocation = YAMLUtil.loadLocationFromYaml(npcData, "location");
        if (npcLocation == null) {
            LogUtil.sendWarnLog("There was an error loading the location for NPC '" + npcUuidString + "'.");
            return null;
        }
        GameProfile gameProfile = new GameProfile(npcUuid, "NPC");
        if (npcData.contains("texture")) {
            String texture = npcData.getString("texture");
            String signature = npcData.getString("signature");
            gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
        }
        List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> npcEquipment = loadNpcEquipment(npcData);
        EntityPlayer entityPlayer = npcManager.loadNpc(npcLocation, gameProfile, npcEquipment);
        // Loading Hologram
        Location hologramLocation = YAMLUtil.loadLocationFromYaml(npcData, "hologram.location");
        hologramLocation.add(0, 0.2, 0);
        Hologram hologram = HologramsAPI.createHologram(plugin, hologramLocation);
        loadHologramLines(npcData, hologram);
        // Loading shop data
        List<Chest> linkedChests = YAMLUtil.loadLinkedChests(npcData);
        List<ShopItem> shopItems = YAMLUtil.loadMachineItems(npcData);
        if (shopType == ShopType.VENDING) {
            double profit = npcData.getDouble("profit");
            return new NPCVendor(entityPlayer, npcEquipment, hologram, linkedChests, shopItems, profit);
        } else {
            double balance = npcData.getDouble("balance");
            return new NPCTrader(entityPlayer, npcEquipment, hologram, linkedChests, shopItems, balance);
        }
    }

    private List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> loadNpcEquipment(FileConfiguration dataFile) {
        String path = "equipment.";
        List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> equipment = new ArrayList<>();
        for (EnumItemSlot enumItemSlot : EnumItemSlot.values()) {
            String subPath = path + enumItemSlot + ".item";
            ItemStack equippedItem = dataFile.getItemStack(subPath);
            net.minecraft.server.v1_16_R3.ItemStack nmsEquippedItem = CraftItemStack.asNMSCopy(equippedItem);

            Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack> equipmentPair =
                    new Pair<>(enumItemSlot, nmsEquippedItem);
            equipment.add(equipmentPair);
        }
        return equipment;
    }

    /**
     * Loads the Hologram text lines from the passed data file to the passed Hologram.
     *
     * @param dataFile FileConfiguration that contains the Hologram's Text Lines.
     * @param hologram Hologram that will be appended the loaded Text Lines.
     */
    private void loadHologramLines(FileConfiguration dataFile, Hologram hologram) {
        List<String> textLines = dataFile.getStringList("hologram.text-lines");
        for (String textLine : textLines) {
            hologram.appendTextLine(textLine);
        }
    }

    /**
     * Goes through all the NPC Shop .yml files on the plugin's folder and loads each NPC based on the UUID fetched
     * from the .yml file name.
     */
    public void loadNpcShops() {
        File npcShopDirectory = new File(plugin.getDataFolder() + "/npc_shops/");
        for (ShopType shopType : ShopType.values()) {
            File npcShopSubdirectory = new File(npcShopDirectory, "/" + shopType.name() + "/");
            if (npcShopSubdirectory.listFiles() == null) continue;
            for (File npcShopFile : npcShopSubdirectory.listFiles()) {
                // Getting the uuid from the .yml file name.
                String npcUuidString = npcShopFile.getName().split("\\.")[0];
                AbstractNPCShop abstractNpcShop = loadNpcShop(npcUuidString, shopType);
                if (abstractNpcShop == null) continue;

                abstractNpcShop.registerNpcShop();
            }
        }
    }

    /**
     * Deletes the passed NPC Shop's .yml file from the plugin's folder.
     *
     * @param abstractNpcShop NPCShop that will be deleted.
     */
    public void deleteNpcShop(AbstractNPCShop abstractNpcShop) {
        NPCShopFile npcShopFile = new NPCShopFile(plugin, abstractNpcShop);
        File dataFile = npcShopFile.getDataFile();
        try {
            dataFile.delete();
        } catch (Exception e) {
            LogUtil.sendWarnLog("There was an error deleting NPCShop with UUID '" +
                    abstractNpcShop.getEntityPlayer().getUniqueIDString() + "'.");
            e.printStackTrace();
        }
    }
}
