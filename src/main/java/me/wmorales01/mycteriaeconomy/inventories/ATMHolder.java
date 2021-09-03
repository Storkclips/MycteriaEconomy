package me.wmorales01.mycteriaeconomy.inventories;

import me.wmorales01.mycteriaeconomy.models.ATM;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ATMHolder implements InventoryHolder {
    private final ATM atm;

    public ATMHolder(ATM atm) {
        this.atm = atm;
    }

    public ATM getAtm() {
        return atm;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

}
