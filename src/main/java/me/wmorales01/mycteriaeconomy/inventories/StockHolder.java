package me.wmorales01.mycteriaeconomy.inventories;

import me.wmorales01.mycteriaeconomy.models.MachineItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class StockHolder implements InventoryHolder {
    private MachineItem machineItem;

    public StockHolder(MachineItem machineItem) {
        this.machineItem = machineItem;
    }

    public MachineItem getMachineItem() {
        return machineItem;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
