package me.wmorales01.mycteriaeconomy.models;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NPCOperator extends MachineOperator {
    private NPCShop shop;

    public NPCOperator(Player player, ItemStack selectedItem, NPCShop shop) {
        super(player, selectedItem);
        this.shop = shop;
    }

    public NPCShop getShop() {
        return shop;
    }

}
