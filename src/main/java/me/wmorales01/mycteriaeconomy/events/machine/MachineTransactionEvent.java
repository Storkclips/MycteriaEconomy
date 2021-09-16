package me.wmorales01.mycteriaeconomy.events.machine;

import me.wmorales01.mycteriaeconomy.models.AbstractMachine;

public class MachineTransactionEvent extends MachineEvent {

    public MachineTransactionEvent(AbstractMachine machine) {
        super(machine);
    }
}
