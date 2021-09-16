package me.wmorales01.mycteriaeconomy.models;

import org.bukkit.block.Chest;
import org.bukkit.inventory.EquipmentSlot;

/**
 * This class allows administrators to configure NPCShops in different ways, depending on the NPCShopOperation some
 * attributes might be null, so that should take into account while working with this class.
 */
public class NPCShopOperator {
    private final NPCShopOperation operation;
    private final Chest linkingChest;
    private final EquipmentSlot configuringSlot;

    public NPCShopOperator(NPCShopOperation operation) {
        this.operation = operation;
        this.linkingChest = null;
        this.configuringSlot = null;
    }

    public NPCShopOperator(NPCShopOperation operation, Chest linkingChest) {
        this.operation = operation;
        this.linkingChest = linkingChest;
        this.configuringSlot = null;
    }

    public NPCShopOperator(NPCShopOperation operation, EquipmentSlot configuringSlot) {
        this.operation = operation;
        this.linkingChest = null;
        this.configuringSlot = configuringSlot;
    }

    public NPCShopOperation getOperation() {
        return operation;
    }

    public Chest getLinkingChest() {
        return linkingChest;
    }

    public EquipmentSlot getConfiguringSlot() {
        return configuringSlot;
    }
}
