package me.spiderdeluxe.mycteriaeconomy.inventories;

import me.spiderdeluxe.mycteriaeconomy.models.Wallet;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class WalletHolder implements InventoryHolder {
    private final Wallet wallet;

    public WalletHolder(final Wallet wallet) {
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
