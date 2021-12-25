package me.spiderdeluxe.mycteriaeconomy.inventories;

import me.spiderdeluxe.mycteriaeconomy.models.atm.ATM;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ATMHolder implements InventoryHolder {
    private final ATM atm;

    public ATMHolder(final ATM atm) {
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
