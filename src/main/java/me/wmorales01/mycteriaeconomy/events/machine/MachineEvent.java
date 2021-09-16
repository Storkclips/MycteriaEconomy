package me.wmorales01.mycteriaeconomy.events.machine;

import me.wmorales01.mycteriaeconomy.models.AbstractMachine;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MachineEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final AbstractMachine machine;

    public MachineEvent(AbstractMachine machine) {
        this.machine = machine;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public AbstractMachine getMachine() {
        return machine;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
