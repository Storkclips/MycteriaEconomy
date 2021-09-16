package me.wmorales01.mycteriaeconomy.models;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.mojang.datafixers.util.Pair;
import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.util.StringUtil;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.ItemStack;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNPCShop extends AbstractShop {
    private final EntityPlayer entityPlayer;
    private final List<Pair<EnumItemSlot, ItemStack>> equipment;
    private final Hologram hologram;

    public AbstractNPCShop(EntityPlayer entityPlayer, String npcName, ShopType shopType) {
        super(shopType);
        this.entityPlayer = entityPlayer;
        this.equipment = new ArrayList<>();
        this.hologram = configureHologram(entityPlayer.getBukkitEntity().getLocation(), npcName);
    }

    public AbstractNPCShop(List<Chest> linkedChests, List<ShopItem> shopItems, ShopType shopType, EntityPlayer entityPlayer,
                           List<Pair<EnumItemSlot, ItemStack>> equipment, Hologram hologram) {
        super(linkedChests, shopItems, shopType);
        this.entityPlayer = entityPlayer;
        this.equipment = equipment;
        this.hologram = hologram;
    }

    /**
     * Seaches for the passed Entity Player in the local global Map of NPC Shops.
     *
     * @param entityPlayer EntityPlayer to search for NPC Shops.
     * @return NPCShop corresponding the passed EntityPlayer, null if there was no corresponding NPCShop.
     */
    public static AbstractNPCShop fromEntityPlayer(EntityPlayer entityPlayer) {
        return MycteriaEconomy.getInstance().getNpcShops().get(entityPlayer);
    }

    /**
     * Configures the passed NPC's Hologram with the passed name
     *
     * @param location Location where the Hologram will be placed.
     * @param npcName  Name of the NPC.
     */
    private Hologram configureHologram(Location location, String npcName) {
        Hologram hologram = HologramsAPI.createHologram(MycteriaEconomy.getInstance(), location.add(0, 2.2, 0));
        hologram.appendTextLine(StringUtil.formatColor(npcName));
        return hologram;
    }

    /**
     * Registers the NPC Shop to the local global List of NPC shops.
     */
    public void registerNpcShop() {
        MycteriaEconomy.getInstance().getNpcShops().put(entityPlayer, this);
    }

    /**
     * Deletes the NPC Shop from the local global List of NPC shops and its respective .yml file.
     */
    public void deleteNpcShop() {
        MycteriaEconomy plugin = MycteriaEconomy.getInstance();
        plugin.getNpcShops().remove(entityPlayer);
        plugin.getNpcDataManager().deleteNpcShop(this);
        new NPCManager(plugin).unloadNpc(this);
        hologram.delete();
    }

    /**
     * Saves the NPC Shop's data to its respective .yml file.
     */
    public void saveShopData() {
        MycteriaEconomy.getInstance().getNpcDataManager().saveNpcShop(this);
    }

    public abstract Inventory getShopGUI();

    public abstract Inventory getOwnerGUI();

    public EntityPlayer getEntityPlayer() {
        return entityPlayer;
    }

    public List<Pair<EnumItemSlot, ItemStack>> getEquipment() {
        return equipment;
    }

    public void addEquipment(Pair<EnumItemSlot, ItemStack> equipment) {
        EnumItemSlot newEquipmentSlot = equipment.getFirst();
        Pair<EnumItemSlot, ItemStack> oldEquipment = null;
        for (Pair<EnumItemSlot, ItemStack> pair : this.equipment) {
            if (!pair.getFirst().equals(newEquipmentSlot)) continue;

            oldEquipment = pair;
            break;
        }
        if (oldEquipment != null) {
            this.equipment.remove(oldEquipment);
        }
        this.equipment.add(equipment);
        saveShopData();
    }

    public Hologram getHologram() {
        return hologram;
    }

    public String getNpcName() {
        HologramLine hologramLine = hologram.getLine(0);
        if (!(hologramLine instanceof TextLine)) return "";

        return ((TextLine) hologramLine).getText();
    }
}
