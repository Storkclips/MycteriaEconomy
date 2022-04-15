package me.spiderdeluxe.mycteriaeconomy.models.bank;


import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.mineacademy.fo.collection.SerializedMap;

import java.util.Set;
import java.util.UUID;

/**
 * This class manages the community banks of the server
 *
 * @author SpiderDeluxe
 */
@Getter
public class CommunityBank extends BaseBank {

    String name;
    OfflinePlayer owner;

    public CommunityBank(final UUID uuid, final String name, final OfflinePlayer owner, final int balance, final Set<Branch> branches) {
        super(BankType.COMMUNITY, uuid, balance, branches);

        this.name = name;
        this.owner = owner;
    }

    public CommunityBank(final String name, final OfflinePlayer owner) {
        super(BankType.COMMUNITY);

        this.name = name;
        this.owner = owner;
    }


    /**
     * Check if a player is an owner of a bank
     *
     * @param owner the player
     */
    public static boolean hasBank(final Player owner) {
        return from(owner) != null;
    }

    /**
     * Check if a CommunityBank already exits with this name
     */
    public static boolean alreadyExist(final String name) {
        return from(name) != null;
    }


    /**
     * Get the CommunityBank by its owner
     *
     * @param owner the owner of bank
     */
    public static CommunityBank from(final OfflinePlayer owner) {
        for (final BaseBank bank : getBanks()) {
            if (bank instanceof CommunityBank communityBank) {
                if (communityBank.getOwner().getUniqueId().equals(owner.getUniqueId())) return communityBank;
            }
        }
        return null;
    }

    /**
     * Get the CommunityBank by its name
     *
     * @param name the name of bank
     */
    public static CommunityBank from(final String name) {
        for (final BaseBank bank : getBanks()) {
            if (bank instanceof CommunityBank communityBank) {
                if (communityBank.getName().equalsIgnoreCase(name)) return communityBank;
            }
        }
        return null;
    }


    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray(
                "UUID", getUuid(),
                "Name", getName(),
                "Owner", getOwner() != null ? getOwner().getUniqueId() : null,
                "Balance", getBalance(),
                "Type", getType(),
                "Vaults", getBranches());
    }
    

}
