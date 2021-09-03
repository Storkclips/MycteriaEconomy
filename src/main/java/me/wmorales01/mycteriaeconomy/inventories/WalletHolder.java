package me.wmorales01.mycteriaeconomy.inventories;

import me.wmorales01.mycteriaeconomy.models.Wallet;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class WalletHolder implements InventoryHolder {
    private final Wallet wallet;

    public WalletHolder(Wallet wallet) {
        this.wallet = wallet;
    }

    public Wallet getWallet() {
        return wallet;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

}
