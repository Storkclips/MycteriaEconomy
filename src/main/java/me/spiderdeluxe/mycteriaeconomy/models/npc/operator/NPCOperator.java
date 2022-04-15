package me.spiderdeluxe.mycteriaeconomy.models.npc.operator;

import lombok.Getter;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.*;

/**
 * This class allows administrators to configure NPCShops in different ways, depending on the NPCShopOperation some
 * attributes might be null, so that should take into account while working with this class.
 */
@Getter
public class NPCOperator {

    /**
     * Stores active operator by their name, so they are singletons
     */
    private static final Map<UUID, NPCOperator> byUUID = new HashMap<>();

    private final Player player;
    private final NPCOperation operation;
    private final Chest linkingChest;
    private final EquipmentSlot configuringSlot;

    public NPCOperator(final Player player, final NPCOperation operation) {
        this.player = player;
        this.operation = operation;
        this.linkingChest = null;
        this.configuringSlot = null;

        byUUID.put(player.getUniqueId(), this);
    }

    public NPCOperator(final Player player, final NPCOperation operation, final Chest linkingChest) {
        this.player = player;
        this.operation = operation;
        this.linkingChest = linkingChest;
        this.configuringSlot = null;

        byUUID.put(player.getUniqueId(), this);
    }

    public NPCOperator(final Player player, final NPCOperation operation, final EquipmentSlot configuringSlot) {
        this.player = player;
        this.operation = operation;
        this.linkingChest = null;
        this.configuringSlot = configuringSlot;

        byUUID.put(player.getUniqueId(), this);
    }



    public void removeOperator() {
        byUUID.remove(this.getPlayer().getUniqueId(), this);
    }


    /**
     * Return an ATM by his location
     **/
    public static NPCOperator getByPlayer(final Player player) {
        for (final NPCOperator operator : getOperators()){
            if (operator.player == player)
                return NPCOperator.byUUID.get(player.getUniqueId());
        }
        return null;
    }



    /**
     * Get all operators
     *
     * @return
     */
    public static Collection<NPCOperator> getOperators() {
        return Collections.unmodifiableCollection(byUUID.values());
    }

    /**
     * Return all operators as their UUID
     */
    public static Set<UUID> getOperatorsUUID() {
        return Collections.unmodifiableSet(byUUID.keySet());
    }

}
