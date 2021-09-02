package me.wmorales01.mycteriaeconomy.inventories;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MachineHolder implements InventoryHolder {
    private Location location;
    private double balance;

    public MachineHolder(Location location) {
        this.location = location;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public Location getLocation() {
        return location;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addBalance(double amount) {
        balance += amount;
    }

    public void discountBalance(double amount) {
        balance -= amount;
    }

}
