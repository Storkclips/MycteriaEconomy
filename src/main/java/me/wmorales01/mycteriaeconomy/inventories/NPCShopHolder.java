package me.wmorales01.mycteriaeconomy.inventories;

import me.wmorales01.mycteriaeconomy.models.NPCShop;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class NPCShopHolder implements InventoryHolder {
    private NPCShop npcShop;
    private double balance;
    private boolean isConfiguring;

    public NPCShopHolder(NPCShop npcShop, boolean isConfiguring) {
        this.npcShop = npcShop;
        this.isConfiguring = isConfiguring;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public NPCShop getNpcShop() {
        return npcShop;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isConfiguring() {
        return isConfiguring;
    }

}
