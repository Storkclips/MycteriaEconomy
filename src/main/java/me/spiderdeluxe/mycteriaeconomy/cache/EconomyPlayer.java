package me.spiderdeluxe.mycteriaeconomy.cache;

import lombok.Getter;
import lombok.Setter;
import me.spiderdeluxe.mycteriaeconomy.models.account.BaseAccount;
import me.spiderdeluxe.mycteriaeconomy.models.account.BusinessAccount;
import me.spiderdeluxe.mycteriaeconomy.models.account.PersonalAccount;
import me.spiderdeluxe.mycteriaeconomy.models.bank.BaseBank;
import me.spiderdeluxe.mycteriaeconomy.models.bank.CommunityBank;
import me.spiderdeluxe.mycteriaeconomy.models.bank.transaction.Transaction;
import me.spiderdeluxe.mycteriaeconomy.models.machine.Machine;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.constants.FoConstants;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.fo.settings.YamlSectionConfig;

import javax.annotation.Nullable;
import java.util.*;

@Getter
public class EconomyPlayer extends YamlSectionConfig {

    private static final HashMap<UUID, EconomyPlayer> cacheMap = new HashMap<>();

    /**
     * This is the uuid of a player
     */
    private final UUID uniqueId;


    /**
     * This instance's player's name
     */
    private final String playerName;

    //
    // Store any custom saveable data here
    //

    /**
     * It stores the set of player's machine
     */
    @Setter
    private Set<Machine> machines = new HashSet<>();

    /**
     * It stores the set of player's accounts
     */
    private Set<BaseAccount> accounts = new HashSet<>();


    /*
     * Creates a new player cache (see the bottom)
     */
    public EconomyPlayer(final String name, final UUID uniqueId) {

        // This will prepend this cache with the players unique id just like you use pathPrefix in SimpleSettings
        super("Players." + uniqueId.toString());

        this.playerName = name;
        this.uniqueId = uniqueId;

        // Load our player cache from the disk however do not use any default file
        // from our source code
        this.loadConfiguration(NO_DEFAULT, FoConstants.File.DATA);
    }

    @Override
    protected void onLoadFinish() {
        machines.clear();

        if (isSet("Economy.Machines")) {
            machines = getSet("Economy.Machines", Machine.class);
        }

        if (isSet("Economy.Accounts")) {
            accounts = getSet("Economy.Accounts", BaseAccount.class);
        }

        if (accounts.isEmpty())
            addAccount(new PersonalAccount(Remain.getPlayerByUUID(uniqueId)));
    }

    /* ------------------------------------------------------------------------------- */
    /* Machine methods */
    /* ------------------------------------------------------------------------------- */


    public void addMachine(final Machine machine) {

        machines.add(machine);
        save("Machines", machines);
    }

    public void removeMachine(final Machine machine) {
        Valid.checkNotNull(machine, "This machine doesn't exists");

        machines.remove(machine);
        save("Machines", machines);
    }



    /* ------------------------------------------------------------------------------- */
    /* Economy methods */
    /* ------------------------------------------------------------------------------- */

    /**
     * Create a bank account and add it to cache
     */
    public void addAccount(final BaseAccount account) {

        Valid.checkNotNull(account, "This account doesn't exists");
        accounts.add(account);


        saveAccounts();
    }

    /**
     * Remove a bank account from cache
     *
     * @param account the account
     */
    public void removeAccount(final BaseAccount account) {
        Valid.checkNotNull(account, "This account doesn't exists");
        accounts.remove(account);

        saveAccounts();
    }

    /**
     * Save the accounts information
     */
    public void saveAccounts() {
        save("Economy.Accounts", accounts);

    }

    public BaseAccount getAccountWithEnoughMoney(final int amount) {
        for (final BaseAccount account : getAccounts()) {
            if (account.getBalance() <= amount) return account;
        }
        return null;
    }

    /**
     * Obtain personal account of a player
     */
    public PersonalAccount getPersonalAccount() {
        for (final BaseAccount account : getAccounts())
            if (account instanceof PersonalAccount)
                return (PersonalAccount) account;
        return null;
    }

    /**
     * Obtain all business counts
     */
    public Set<BusinessAccount> getBusinessAccount() {
        final Set<BusinessAccount> businessAccounts = new HashSet<>();
        for (final BaseAccount account : getAccounts()) {
            if (account instanceof BusinessAccount) {
                final BusinessAccount businessAccount = (BusinessAccount) account;
                businessAccounts.add(businessAccount);
            }
        }
        return businessAccounts;
    }


    /**
     * Obtain all bank with permission
     */
    public Set<BaseBank> getBanks() {
        final Player player = Bukkit.getPlayer(playerName);

        final Set<BaseBank> banks = new HashSet<>(BaseBank.getBanks());

        banks.removeIf(bank -> {
            if (player.hasPermission(Settings.General.PERMISSION_EDIT_ALLBANKS)) return false;


            if (bank instanceof CommunityBank communityBank) {
                return (communityBank.getOwner() != player);
            } else
                return !player.hasPermission(Settings.General.PERMISSION_EDIT_MAINBANKS);
        });

        return banks;
    }


    /**
     * Obtain all transactions of counts
     *
     * @return a list with transactions
     */
    public List<Transaction> getCountsTransaction() {
        final List<Transaction> transactions = new ArrayList<>();
        for (final BaseAccount baseAccount : getAccounts()) {
            transactions.addAll(Transaction.getAccountTransaction(baseAccount));
        }
        
        return transactions;
    }


    public List<BaseAccount> getAccountInOrder() {
        final List<BaseAccount> accounts = new ArrayList<>();
        accounts.add(getPersonalAccount());
        accounts.addAll(getBusinessAccount());
        return accounts;
    }

    /**
     * Obtain all number of counts
     *
     * @return a list with counts numbers as Integer
     */
    public List<Integer> getCountsNumbers() {
        return Common.convert(getAccounts(), BaseAccount::getCountNumber);
    }

    /**
     * Obtain all uuid of counts
     *
     * @return a list with counts uuids as UUID
     */
    public List<UUID> getCountsUUID() {
        final List<UUID> countsNumbers = new ArrayList<>();
        for (final BaseAccount baseAccount : getAccounts()) {
            countsNumbers.add(baseAccount.getUuid());
        }
        return countsNumbers;
    }

    /* ------------------------------------------------------------------------------- */
    /* Misc methods */
    /* ------------------------------------------------------------------------------- */

    /**
     * Return player from cache if online or null otherwise
     *
     * @return
     */
    @Nullable
    public Player toPlayer() {
        final Player player = Remain.getPlayerByUUID(this.uniqueId);

        return player != null && player.isOnline() ? player : null;
    }

    /**
     * Remove this cached data from memory if it exists
     */
    public void removeFromMemory() {
        synchronized (cacheMap) {
            cacheMap.remove(this.uniqueId);
        }
    }

    /**
     * @see org.mineacademy.fo.settings.YamlSectionConfig#toString()
     */
    @Override
    public String toString() {
        return "PlayerCache{" + this.playerName + ", " + this.uniqueId + "}";
    }

    /* ------------------------------------------------------------------------------- */
    /* Static access */
    /* ------------------------------------------------------------------------------- */

    /**
     * Return or create new player cache for the given player's uuid
     *
     * @param uuid the uuid
     */
    public static EconomyPlayer from(final UUID uuid) {
        return EconomyPlayer.from(Remain.getOfflinePlayerByUUID(uuid));
    }

    /**
     * Return or create new player cache for the given player
     *
     * @param player the player
     */
    public static EconomyPlayer from(final OfflinePlayer player) {
        synchronized (cacheMap) {
            final UUID uniqueId = player.getUniqueId();
            final String playerName = player.getName();

            EconomyPlayer cache = cacheMap.get(uniqueId);

            if (cache == null) {
                cache = new EconomyPlayer(playerName, uniqueId);

                cacheMap.put(uniqueId, cache);
            }

            return cache;
        }
    }

    /**
     * Clear the entire cache map
     */
    public static void clear() {
        synchronized (cacheMap) {
            cacheMap.clear();
        }
    }
}

