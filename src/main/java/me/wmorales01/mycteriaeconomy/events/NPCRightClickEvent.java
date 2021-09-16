package me.wmorales01.mycteriaeconomy.events;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NPCRightClickEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final EntityPlayer entityPlayer;

    public NPCRightClickEvent(Player player, EntityPlayer entityPlayer) {
        this.player = player;
        this.entityPlayer = entityPlayer;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public EntityPlayer getEntityPlayer() {
        return entityPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}