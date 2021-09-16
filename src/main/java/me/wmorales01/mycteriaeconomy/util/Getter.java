package me.wmorales01.mycteriaeconomy.util;

import me.wmorales01.mycteriaeconomy.models.CurrencyItem;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Getter {

    public static Player getPlayer(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            return null;
        }

        return player;
    }

    public static int getValueFromBill(ItemStack bill) {
        ItemMeta meta = bill.getItemMeta();
        if (!meta.hasCustomModelData())
            return 0;
        int modelData = meta.getCustomModelData();
        switch (modelData) {
            case 101:
                return 1 * bill.getAmount();

            case 102:
                return 5 * bill.getAmount();

            case 103:
                return 10 * bill.getAmount();

            case 104:
                return 20 * bill.getAmount();

            case 105:
                return 50 * bill.getAmount();

            case 106:
                return 100 * bill.getAmount();

            default:
                return 0 * bill.getAmount();
        }
    }

    public static double getValueFromCoin(ItemStack coin) {
        ItemMeta meta = coin.getItemMeta();
        if (!meta.hasCustomModelData())
            return 0;
        int modelData = meta.getCustomModelData();

        switch (modelData) {
            case 101:
                return 0.01 * coin.getAmount();

            case 102:
                return 0.05 * coin.getAmount();

            case 103:
                return 0.10 * coin.getAmount();

            case 104:
                return 0.25 * coin.getAmount();

            default:
                return 0;

        }
    }

    public static ItemStack getCurrencyFromValue(double value) {
        CurrencyItem items = new CurrencyItem();
        if (value == 100)
            return items.oneHundredDollarBill();
        else if (value == 50)
            return items.fiftyDollarBill();
        else if (value == 20)
            return items.twentyDollarBill();
        else if (value == 10)
            return items.tenDollarBill();
        else if (value == 5)
            return items.fiveDollarBill();
        else if (value == 1)
            return items.oneDollarBill();
        else if (value == 0.25)
            return items.twentyFiveCentCoin();
        else if (value == 0.10)
            return items.tenCentCoin();
        else if (value == 0.05)
            return items.fiveCentCoin();
        else if (value == 0.01)
            return items.oneCentCoin();

        return null;
    }

    public static ItemStack[] getMachineStock(Inventory inventory) {
        List<ItemStack> toRemove = new ArrayList<ItemStack>();
        ItemStack[] machineStock = inventory.getContents();

        for (int i = 0; i < machineStock.length; i++) {
            ItemStack item = machineStock[i];
            if (item == null)
                continue;
            if (!item.hasItemMeta())
                continue;
            if (!item.getItemMeta().hasDisplayName())
                continue;
            if (!item.getItemMeta().getDisplayName().equalsIgnoreCase(" "))
                continue;

            toRemove.add(item);
        }

        for (ItemStack item : toRemove)
            machineStock = (ItemStack[]) ArrayUtils.removeElement(machineStock, item);

        return machineStock;
    }

    public static Double getDoubleFromString(CommandSender sender, String toConvert) {
        Double number = 0.0;
        try {
            number = Double.parseDouble(toConvert);
        } catch (Exception e) {
            Messager.sendMessage(sender, "&cYou must enter a numeric amount.");
            return null;
        }

        return number;
    }

    public static Integer getIntFromString(CommandSender sender, String toConvert) {
        Integer number = 0;
        try {
            number = Integer.parseInt(toConvert);
        } catch (Exception e) {
            Messager.sendMessage(sender, "&cYou must enter a numeric amount.");
            return null;
        }

        return number;
    }


}
