package me.spiderdeluxe.mycteriaeconomy.models.bank;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.spiderdeluxe.mycteriaeconomy.cache.DataStorage;
import me.spiderdeluxe.mycteriaeconomy.models.Wallet;
import me.spiderdeluxe.mycteriaeconomy.models.account.BaseAccount;
import me.spiderdeluxe.mycteriaeconomy.models.bank.transaction.Transaction;
import me.spiderdeluxe.mycteriaeconomy.models.bank.transaction.TransactionType;
import me.spiderdeluxe.mycteriaeconomy.util.BalanceUtil;
import me.spiderdeluxe.mycteriaeconomy.util.CurrencyItem;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import me.spiderdeluxe.mycteriaeconomy.util.SFXManager;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.remain.Remain;

import java.util.*;

/**
 * This class manages the baseBanks of the server
 *
 * @author SpiderDeluxe
 */
@Getter
public class BaseBank implements ConfigSerializable {

    /**
     * Stores active account by their uuid, so they are singletons
     */
    private static final Map<UUID, BaseBank> byUUID = new HashMap<>();


    /**
     * The unique uuid of bank3.
     */
    private final UUID uuid;


    /**
     * Money held by the bank
     */
    @Setter
    private int balance;

    /**
     * Type of banking account
     */
    @Setter
    private BankType type;

    /**
     * PhysicsBanks
     */
    private Set<Branch> branches = new HashSet<>();
    
    public BaseBank(final BankType type, final UUID uuid, final int balance, final Set<Branch> branches) {
        this.uuid = uuid;
        this.balance = balance;
        this.branches = branches;

        setType(type);

        createBank();
    }


    public BaseBank(final BankType type) {
        this.uuid = createBankUUID();

        setType(type);

        createBank();
    }


    // --------------------------------------------------------------------------------------------------------------
    // Bank Manipulation
    // --------------------------------------------------------------------------------------------------------------

    public void createBank() {
        final DataStorage dataStorage = DataStorage.getInstance();
        dataStorage.addBank(this);

        byUUID.put(uuid, this);
    }

    public void deleteBank() {
        final DataStorage dataStorage = DataStorage.getInstance();
        dataStorage.removeBank(this);

        byUUID.remove(this.getUuid(), this);
    }


    /**
     * Used to add physicals branch
     */
    public void addBranch(final String name) {
        final Branch branch = new Branch();
        branch.setName(name);
        branches.add(branch);
    }

    public void deleteBranch(final Branch branch) {
        branches.remove(branch);
    }


    /**
     * Increase the balance of count
     *
     * @param amount the amount
     */
    public void increaseBalance(final int amount) {

        this.balance += amount;
    }

    /**
     * Decrease the balance of count
     *
     * @param amount the amount
     */
    public void decreaseBalance(final int amount) {
        this.balance -= amount;
    }

    // --------------------------------------------------------------------------------------------------------------
    // Bank transaction
    // --------------------------------------------------------------------------------------------------------------

    /**
     * Withdraw money
     */
    public void withdraw(final BaseAccount account, final int amount) {
        Transaction.createTransaction(TransactionType.WITHDRAW, amount, account, null);
        decreaseBalance(amount);
    }

    /**
     * Withdraw money
     */
    public void deposit(final BaseAccount account, final int amount) {
        Transaction.createTransaction(TransactionType.DEPOSIT, amount, account, null);
        increaseBalance(amount);
    }

    /**
     * Withdraws the value of the passed economyItem from the passed economyPlayer's bank balance (if possible)
     * and reopens the passed atm's GUI
     */
    public void withdraw(final BaseAccount account, final ItemStack economyItem) {
        final Player player = account.getOwner();

        if (player == null) return;

        final int withdrawValue = (int) CurrencyItem.getValueFromItem(economyItem);
        if ((this.getBalance() - withdrawValue) < 0) {
            Messager.sendErrorMessage(player, "&cYou don't have enough bank balance to execute this transaction.");
            return;
        }

        withdraw(account, withdrawValue);

        final ItemStack withdrawnItem = CurrencyItem.getItemFromValue(withdrawValue);
        if (withdrawnItem == null) return;
        final Location playerLocation = player.getLocation();
        if (player.getInventory().firstEmpty() == -1) {
            playerLocation.getWorld().dropItemNaturally(playerLocation, withdrawnItem);
        } else {
            player.getInventory().addItem(withdrawnItem);
        }
        SFXManager.playWorldSound(playerLocation, Sound.BLOCK_NOTE_BLOCK_BIT, 0.7F, 1.3F);
    }


    /**
     * Deposit the passed balanceItem's value to the passed economyPlayer's bank account and reopens the
     * passed atm's GUI after deleting the clicked economyItem
     */
    public void deposit(final BaseAccount account, final ItemStack economyItem) {
        final double depositValue = CurrencyItem.getValueFromItem(economyItem);

        final Player player = account.getOwner();

        // Executing transaction
        final Inventory inventory = player.getInventory();
        final ItemStack walletItem = Wallet.findItemWalletInInv(inventory);
        final int walletSlot = Wallet.findSlotWalletInInv(inventory);
        final Wallet wallet = Wallet.fromItemStack(walletItem);

        final double availableCash;

        if (wallet == null
                || wallet.getBalance() <= 0) {
            availableCash = BalanceUtil.computeInventoryBalance(player.getInventory());
        } else {
            availableCash = wallet.getBalance();
        }

        // Checking if the player has enough cash
        if (availableCash < depositValue) {
            Messager.sendErrorMessage(player, "&cYou don't have enough funds to execute this transaction.");
            return;
        }

        // Executing Transaction
        if (wallet == null
                || wallet.getBalance() < 1) {
            BalanceUtil.removeBalance(player.getInventory(), depositValue);
            player.updateInventory();
        } else {
            wallet.decreaseBalance(depositValue);
            inventory.setItem(walletSlot, wallet.getItemStack());
        }
        deposit(account, (int) (depositValue));
        SFXManager.playWorldSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.7F, 1.3F);
    }

    // --------------------------------------------------------------------------------------------------------------
    // Static access
    // --------------------------------------------------------------------------------------------------------------


    /**
     * Check if a player is inside region of bank and send error message
     *
     * @param player the player
     * @param silent if send message
     */
    public static boolean isWithin(final Player player, final boolean silent) {
        if (from(player.getLocation()) == null) {
            if (silent)
                Messenger.error(player, "You are not within the bank area, so you cannot take this action.");
            return false;
        }
        return true;
    }

    /**
     * Check if in this location there is a bank
     *
     * @param location the location
     */
    public static boolean isWithin(final Location location) {
        return from(location) != null;
    }


    /**
     * Create an uuid to identify the banjk by verifying that there is no identical one already present
     *
     * @return the BaseBank's uuid
     */
    public static UUID createBankUUID() {
        UUID uuid = UUID.randomUUID();
        while (getBanksUUIDs().contains(uuid)) {
            uuid = UUID.randomUUID();
        }
        return uuid;
    }

    /**
     * Get the BaseBank by its uuid
     *
     * @param uuid the uuid of bank
     */
    public static BaseBank from(final UUID uuid) {
        for (final BaseBank bank : getBanks()) {
            if (bank.getUuid().equals(uuid))
                return bank;
        }
        return null;
    }

    /**
     * Get the BaseBank by its location
     *
     * @param location the location of bank
     */
    public static BaseBank from(final Location location) {
        for (final BaseBank bank : getBanks()) {
            for (final Branch branch : bank.branches) {
                if (branch.getRegion() != null) if (branch.getRegion().isWithin(location)) return bank;
            }
        }
        return null;
    }


    /**
     * Return all BaseBank
     */
    public static Collection<BaseBank> getBanks() {
        return Collections.unmodifiableCollection(byUUID.values());
    }

    /**
     * Return all name of BaseBank
     *
     * @return a collection of players
     */
    public static Collection<UUID> getBanksUUIDs() {
        return Collections.unmodifiableCollection(byUUID.keySet());
    }


    // --------------------------------------------------------------------------------------------------------------
    // Serialization method
    // --------------------------------------------------------------------------------------------------------------


    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray(
                "UUID", uuid,
                "Balance", balance,
                "Type", type,
                "Vaults", branches);
    }

    @SneakyThrows
    public static BaseBank deserialize(final SerializedMap map) {
        final UUID uuid = map.getUUID("UUID");

        final int balance = map.getInteger("Balance");

        final BankType type = BankType.fromName(map.getString("Type"));

        String name = null;
        if (map.containsKey("Name")) {
            name = map.getString("Name");
        }

        OfflinePlayer owner = null;
        if (map.containsKey("Owner")) {
            owner = Remain.getOfflinePlayerByUUID(map.getUUID("Owner"));
        }

        final Set<Branch> branches = map.getSet("Vaults", Branch.class);

        return switch (type) {
            case STATE -> new StateBank(uuid, balance, branches);
            case LOCAL -> new LocalBank(uuid, balance, branches);
            case COMMUNITY -> new CommunityBank(uuid, name, owner, balance, branches);
        };
    }
}
