package me.wmorales01.mycteriaeconomy.files;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.MachineItem;
import me.wmorales01.mycteriaeconomy.models.NPCManager;
import me.wmorales01.mycteriaeconomy.models.NPCShop;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCDataManager {
    private MycteriaEconomy plugin;

    public NPCDataManager(MycteriaEconomy plugin) {
        this.plugin = plugin;
    }

    public void saveAllNPCs() {
        for (NPCShop shop : plugin.getNpcs())
            saveNPC(shop);
    }

    public void saveNPC(NPCShop shop) {
        FileConfiguration data = plugin.getNPCData();
        EntityPlayer npc = shop.getNpc();
        Location location = npc.getBukkitEntity().getLocation();
        GameProfile profile = npc.getProfile();
        Property property = null;
        String[] properties = new String[2];
        if (!profile.getProperties().get("textures").isEmpty()) {
            property = profile.getProperties().get("textures").iterator().next();
            properties[0] = property.getValue();
            properties[1] = property.getSignature();
        }
        String path = "npcs." + npc.getUniqueIDString();
        data.set(path + ".x", location.getX());
        data.set(path + ".y", location.getY());
        data.set(path + ".z", location.getZ());
        data.set(path + ".pitch", npc.pitch);
        data.set(path + ".yaw", npc.yaw);
        data.set(path + ".world", location.getWorld().getName());
        if (properties[0] != null && properties[1] != null) {
            data.set(path + ".texture", properties[0]);
            data.set(path + ".signature", properties[1]);
        }
        List<Location> chestLocations = shop.getChestLocations();
        int chestVar = 1;
        for (Location chestLocation : chestLocations) {
            data.set(path + ".chest-locations" + "." + chestVar + ".x", chestLocation.getX());
            data.set(path + ".chest-locations" + "." + chestVar + ".y", chestLocation.getY());
            data.set(path + ".chest-locations" + "." + chestVar + ".z", chestLocation.getZ());
            data.set(path + ".chest-locations" + "." + chestVar + ".world", chestLocation.getWorld().getName());
            chestVar++;
        }
        List<MachineItem> stock = shop.getStock();
        int varItem = 1;
        for (MachineItem machineItem : stock) {
            ItemStack item = machineItem.getItemStack();
            double sellPrice = machineItem.getPrice();
            int sellAmount = machineItem.getSellAmount();
            int stockAmount = machineItem.getStockAmount();

            data.set(path + ".stock." + varItem + ".item", item);
            data.set(path + ".stock." + varItem + ".sell-price", sellPrice);
            data.set(path + ".stock." + varItem + ".sell-amount", sellAmount);
            data.set(path + ".stock." + varItem + ".stock-amount", stockAmount);
            varItem++;
        }
        plugin.saveNPCData();
    }

    public void restoreNpcData() {
        FileConfiguration data = plugin.getNPCData();
        NPCManager manager = new NPCManager(plugin);
        ConfigurationSection section = data.getConfigurationSection("npcs");
        if (section == null)
            return;

        section.getKeys(false).forEach(npc -> {
            // Getting Location
            World world = Bukkit.getWorld(section.getString(npc + ".world"));
            double x = section.getDouble(npc + ".x");
            double y = section.getDouble(npc + ".y");
            double z = section.getDouble(npc + ".z");
            float pitch = (float) section.getDouble(npc + ".pitch");
            float yaw = (float) section.getDouble(npc + ".yaw");
            Location location = new Location(world, x, y, z);
            location.setPitch(pitch);
            location.setYaw(yaw);

            // Getting profile and skin
            GameProfile gameProfile = new GameProfile(UUID.fromString(npc), "");
            if (section.getString(npc + ".texture") != null) {
                String texture = section.getString(npc + ".texture");
                String signature = section.getString(npc + ".signature");
                gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
            }
            ConfigurationSection chestSection = section
                    .getConfigurationSection(npc + ".chest-locations");
            List<Location> chestLocations = new ArrayList<>();
            if (chestSection != null) {
                chestSection.getKeys(false).forEach(chestLocation -> {
                    int chestX = chestSection.getInt(chestLocation + ".x");
                    int chestY = chestSection.getInt(chestLocation + ".y");
                    int chestZ = chestSection.getInt(chestLocation + ".z");
                    World chestWorld = Bukkit.getWorld(chestSection.getString(chestLocation + ".world"));
                    chestLocations.add(new Location(chestWorld, chestX, chestY, chestZ));
                });
            }
            List<MachineItem> stock = loadMachineStock("npcs." + npc);
            manager.loadNpc(location, gameProfile, chestLocations, stock);
        });
    }

    public void deleteNPC(NPCShop shop) {
        plugin.getNPCData().set("npcs." + shop.getNpc().getUniqueIDString(), null);
        plugin.saveNPCData();
    }

    private List<MachineItem> loadMachineStock(String dataPath) {
        FileConfiguration data = plugin.getNPCData();
        ConfigurationSection stockSection = data.getConfigurationSection(dataPath + ".stock");
        List<MachineItem> stock = new ArrayList<>();
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
