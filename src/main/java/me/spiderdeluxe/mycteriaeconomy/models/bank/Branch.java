package me.spiderdeluxe.mycteriaeconomy.models.bank;

import lombok.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.visual.VisualizedRegion;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class Branch implements ConfigSerializable {


    @Setter
    private String name;

    private VisualizedRegion region;

    /**
     * The editing player
     */
    @Getter
    @Setter
    private Player editingPlayer;


    public void setEditingPlayer(final Player player) {
        this.editingPlayer = editingPlayer == player ? null : player;
    }

    /**
     * Sets or updates the region point
     *
     * @param primary
     * @param secondary
     */
    public void setRegion(final Location primary, final Location secondary) {
        // Update region points
        if (this.region != null)
            this.region.updateLocationsWeak(primary, secondary);

        else
            this.region = new VisualizedRegion(primary, secondary);
    }


    /**
     * Get the Branch by its location
     *
     * @param location the location of branch
     */
    public static Branch from(final Location location) {
        for (final BaseBank bank : BaseBank.getBanks()) {
            for (final Branch branch : bank.getBranches())
                if (branch.getRegion().isWithin(location))
                    return branch;
        }
        return null;
    }

    /**
     * Check if a player editing
     *
     * @param player the player
     */
    public static boolean isEditingPlayer(final Player player) {
        return getEditingBranch(player) != null;
    }

    /**
     * Obtain branch from a player.
     *
     * @param player the player
     */
    public static Branch getEditingBranch(final Player player) {
        for (final BaseBank bank : BaseBank.getBanks()) {
            for (final Branch branch : bank.getBranches()) {
                if (branch.editingPlayer == player)
                    return branch;
            }
        }
        return null;
    }


    public static Branch deserialize(final SerializedMap map) {
        final Branch branch = new Branch();

        branch.name = map.getString("Name");
        branch.region = map.get("Region", VisualizedRegion.class);
        return branch;
    }

    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray(
                "Name", name,
                "Region", region);
    }

    public String getRegionString() {
        return Common.shortLocation(region.getPrimary()) + " - " + Common.shortLocation(region.getSecondary());
    }
}
