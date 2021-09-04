package me.wmorales01.mycteriaeconomy.models;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.files.ConfigManager;
import me.wmorales01.mycteriaeconomy.inventories.ATMHolder;
import me.wmorales01.mycteriaeconomy.util.GUIUtil;
import me.wmorales01.mycteriaeconomy.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ATM {
    private final UUID uuid;
    private final Location location;

    public ATM(Location location) {
        this.uuid = UUID.randomUUID();
        this.location = location;
    }

    public ATM(UUID uuid, Location location) {
        this.uuid = uuid;
        this.location = location;
    }

    public static ATM fromLocation(Location location) {
        return MycteriaEconomy.getInstance().getAtms().get(location);
    }

    public static ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.DISPENSER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(StringUtil.formatColor("&eATM"));
        List<String> lore = new ArrayList<>();
        lore.add(StringUtil.formatColor("&6&lPlace this block to install a new ATM"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void registerATM() {
        MycteriaEconomy.getInstance().getAtms().put(location, this);
        saveATMData();
    }

    public void unregisterATM() {
        MycteriaEconomy plugin = MycteriaEconomy.getInstance();
        plugin.getAtms().remove(location);
        plugin.getAtmManager().deleteATM(this);
    }

    private void saveATMData() {
        MycteriaEconomy.getInstance().getAtmManager().saveATM(this);
    }

    public Inventory getWithdrawATMGUI(EconomyPlayer economyPlayer) {
        Inventory inventory = Bukkit.createInventory(new ATMHolder(this), 36, "ATM");
        GUIUtil.setFrame(inventory, Material.LIME_STAINED_GLASS_PANE);
        // Filling slot 10, 16, 19 and 25
        ItemStack filler = GUIUtil.getFiller(Material.LIME_STAINED_GLASS_PANE);
        inventory.setItem(10, filler);
        inventory.setItem(16, filler);
        inventory.setItem(19, filler);
        inventory.setItem(25, filler);
        addEconomyItems(inventory);
        addBalanceItem(inventory, economyPlayer.getBankBalance());
        addGuideItem(inventory);
        addFeeItem(inventory);
        return inventory;
    }

    private void addEconomyItems(Inventory inventory) {
        inventory.setItem(22, getATMItem(EconomyItem.oneHundredDollarBill()));
        for (ItemStack economyItem : EconomyItem.getEconomyItems()) {
            if (EconomyItem.getValueFromItem(economyItem) == 100) continue;

            inventory.addItem(getATMItem(economyItem));
        }
    }

    private ItemStack getATMItem(ItemStack economyItem) {
        ItemMeta meta = economyItem.getItemMeta();
        meta.setDisplayName(StringUtil.formatColor("&2&l$" +
                StringUtil.roundNumber(EconomyItem.getValueFromItem(economyItem), 2)));
        meta.setLore(Arrays.asList(StringUtil.formatColor("&eClick here to withdraw.")));
        economyItem.setItemMeta(meta);
        return economyItem;
    }

    private void addBalanceItem(Inventory inventory, double balance) {
        ItemStack balanceItem = GUIUtil.getGUIItem(Material.SUNFLOWER,
                StringUtil.formatColor("&aBalance: &a&l$" + StringUtil.roundNumber(balance, 2)), null);
        inventory.setItem(30, balanceItem);
    }

    private void addGuideItem(Inventory inventory) {
        List<String> lore = new ArrayList<>();
        lore.add("&eYou can click a bill or coin from your");
        lore.add("&einventory to deposit it into your balance.");
        ItemStack guideItem = GUIUtil.getGUIItem(Material.COMPASS, "&2Information", lore);
        inventory.setItem(31, guideItem);
    }

    private void addFeeItem(Inventory inventory) {
        double transactionFee = ConfigManager.getAtmTranstacionFee();
        ItemStack feeItem = GUIUtil.getGUIItem(Material.BLAZE_POWDER, "&eTransaction Fee: ",
                Arrays.asList("&c &l$" + StringUtil.roundNumber(transactionFee, 2)));
        inventory.setItem(32, feeItem);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Location getLocation() {
        return this.location;
    }
}
