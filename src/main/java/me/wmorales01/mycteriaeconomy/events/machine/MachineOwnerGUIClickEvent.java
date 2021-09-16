package me.wmorales01.mycteriaeconomy.events.machine;

import me.wmorales01.mycteriaeconomy.models.AbstractMachine;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MachineOwnerGUIClickEvent extends MachineEvent {
    private final InventoryClickEvent clickEvent;

    public MachineOwnerGUIClickEvent(AbstractMachine machine, InventoryClickEvent clickEvent) {
        super(machine);
        this.clickEvent = clickEvent;
    }

    public InventoryClickEvent getClickEvent() {
        return clickEvent;
    }
}
