package me.wmorales01.mycteriaeconomy.models;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public abstract class AbstractMachine extends AbstractShop {
    private final UUID machineUuid;
    private final UUID ownerUuid;
    private final Location machineLocation;

    public AbstractMachine(Player owner, Block machineBlock) {
        super(ShopType.VENDING);
        this.machineUuid = UUID.randomUUID();
        this.ownerUuid = owner.getUniqueId();
        this.machineLocation = machineBlock.getLocation();
    }

    public AbstractMachine(List<Chest> linkedChests, List<ShopItem> shopItems, UUID machineUuid, UUID ownerUuid,
                           Location machineLocation) {
        super(linkedChests, shopItems, ShopType.VENDING);
        this.machineUuid = machineUuid;
        this.ownerUuid = ownerUuid;
        this.machineLocation = machineLocation;
    }

    /**
     * Searches the passed location in the global Map of stored machines and returns its corresponding machine.
     *
     * @param location location that will be searched for.
     * @return Machine instance that exists on the passed location, null if it couldn't find one.
     */
    public static AbstractMachine fromLocation(Location location) {
        return MycteriaEconomy.getInstance().getMachines().get(location);
    }

    /**
     * Registers the AbstractMachine to the local global Map of machines.
     */
    public void registerMachine() {
        MycteriaEconomy.getInstance().getMachines().put(machineLocation, this);
    }

    /**
     * Deletes the AbstractMachine from the local global Map of machines and its respective .yml file.
     */
    public void deleteMachine() {
        MycteriaEconomy plugin = MycteriaEconomy.getInstance();
        plugin.getMachines().remove(machineLocation);
        plugin.getMachineManager().deleteMachine(this);
    }

    /**
     * Saves all the data from the current instance of AbstractMachine to its respective .yml file
     */
    public void saveShopData() {
        MycteriaEconomy.getInstance().getMachineManager().saveMachine(this);
    }

    public UUID getMachineUuid() {
        return machineUuid;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public boolean isOwner(Player player) {
        return player.getUniqueId().equals(ownerUuid);
    }

    public Location getMachineLocation() {
        return machineLocation;
    }
}
