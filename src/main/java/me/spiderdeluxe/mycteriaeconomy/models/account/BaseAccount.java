package me.spiderdeluxe.mycteriaeconomy.models.account;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.models.bank.BaseBank;
import me.spiderdeluxe.mycteriaeconomy.models.bank.Loan;
import me.spiderdeluxe.mycteriaeconomy.models.bank.transaction.Transaction;
import me.spiderdeluxe.mycteriaeconomy.models.bank.transaction.TransactionType;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.remain.Remain;

import java.util.*;

@Getter
public class BaseAccount implements ConfigSerializable {
    /**
     * Stores active account by their uuid, so they are singletons
     */
    private static final Map<UUID, BaseAccount> byUUID = new HashMap<>();

    private static final Set<Integer> countNumbers = new HashSet<>();


    /**
     * The unique uuid of bank Account.
     */
    private final UUID uuid;

    /**
     * The number of count
     */
    private int countNumber;

    /**
     * The owner of this account
     */
    private final OfflinePlayer owner;

    /**
     * The trusted of this account
     */
    private OfflinePlayer trusted;

    /**
     * The balance of this account
     */
    @Setter
    private int balance;


    /**
     * Set of active loans
     */
    @Setter
    @Getter
    private Set<Loan> loans = new HashSet<>();

    /**
     * Set of stored Transaction
     */
    @Getter(AccessLevel.NONE)
    private Set<Transaction> transactions = new HashSet<>();


    /**
     * Type of banking account
     */
    @Setter
    private AccountType type;

    public BaseAccount(final UUID uuid, final int countNumber, final OfflinePlayer owner, final OfflinePlayer trusted, final int balance, final Set<Loan> loans, final AccountType type, final Set<Transaction> transactions) {
        this.uuid = uuid;
        this.countNumber = countNumber;
        this.owner = owner;
        this.trusted = trusted;
        this.balance = balance;
        this.loans = loans;
        this.type = type;
        this.transactions = transactions;

        createAccount(false);
    }

    public BaseAccount(final UUID uuid, final Player owner) {
        this.uuid = uuid;
        this.owner = owner;

        createAccount(true);
    }

    public BaseAccount(final Player owner) {
        this.uuid = createAccountUUID();
        this.owner = owner;

        createAccount(true);
    }


    // --------------------------------------------------------------------------------------------------------------
    // Account Manipulation
    // --------------------------------------------------------------------------------------------------------------

    public void createAccount(final Boolean initial) {

        if (initial) generateCountNumber();


        countNumbers.add(countNumber);
        byUUID.put(uuid, this);
    }

    public void deleteAccount() {
        final BaseAccount account = this;


        byUUID.remove(account.getUuid(), account);
    }


    public void saveAccount() {
        final EconomyPlayer economyPlayer = EconomyPlayer.from(getOwner());
        economyPlayer.saveAccounts();
    }


    // --------------------------------------------------------------------------------------------------------------
    // Transactions Manipulation
    // --------------------------------------------------------------------------------------------------------------

    public void addTransaction(final Transaction transaction) {
        transactions.add(transaction);
        saveAccount();
    }

    public void removeTransaction(final Transaction transaction) {
        transactions.remove(transaction);
        saveAccount();
    }

    // --------------------------------------------------------------------------------------------------------------
    // Loan Manipulation
    // --------------------------------------------------------------------------------------------------------------

    public void addLoan(final Loan loan) {
        loans.add(loan);
        saveAccount();
    }

    public void removeLoan(final Loan loan) {
        loans.remove(loan);
        saveAccount();
    }
    // --------------------------------------------------------------------------------------------------------------
    // Balance Manipulation
    // --------------------------------------------------------------------------------------------------------------


    /**
     * This method is used to pay someone and register the transaction
     *
     * @param paid     the player that was paid
     * @param amount   the amount
     * @param location the location of player
     */
    public void payAccount(final TransactionType type, final int amount, final BaseAccount paid, final Location location) {
        Transaction.createTransaction(type, amount, this, paid);

        final int commission = (amount * getCommission()) / 100;
        decreaseBalance(amount + commission);

        if (BaseBank.isWithin(location)) {
            final BaseBank bank = BaseBank.from(location);
            bank.increaseBalance(commission);
        }

        paid.increaseBalance(amount);
    }

    /**
     * Withdraw money
     */
    public void withdraw(final int amount) {
        Transaction.createTransaction(TransactionType.WITHDRAW, amount, this, null);
        decreaseBalance(amount);
    }

    /**
     * Withdraw money
     */
    public void deposit(final int amount) {
        Transaction.createTransaction(TransactionType.DEPOSIT, amount, this, null);
        increaseBalance(amount);
    }

    /**
     * Increase the balance of count
     *
     * @param amount the amount
     */
    public void increaseBalance(final int amount) {

        this.balance += amount;
        saveAccount();
    }

    /**
     * Decrease the balance of count
     *
     * @param amount the amount
     */
    public void decreaseBalance(final int amount) {
        this.balance -= amount;
        saveAccount();
    }


    /**
     * Generate the number of count
     * Between 10000 and 99999
     */
    private void generateCountNumber() {
        countNumber = RandomUtil.nextBetween(10000, 99999);
        while (countNumbers.contains(countNumber)) {
            countNumber = RandomUtil.nextBetween(10000, 99999);
        }
    }

    public Player getOwner() {
        return owner.getPlayer();
    }

    public Player getTrusted() {
        return trusted.getPlayer();
    }


// --------------------------------------------------------------------------------------------------------------
    // Commission system access
    // --------------------------------------------------------------------------------------------------------------


    /**
     * Every transaction has a commission. (For personal account is 10%).
     *
     * @return the commission
     */
    public int getCommission() {
        return Settings.General.DEFAULT_COMMISSION_FEE_PERCENTAGE;
    }

    // --------------------------------------------------------------------------------------------------------------
    // Static access
    // --------------------------------------------------------------------------------------------------------------

    /**
     * Check if a account is of a player
     */
    public boolean isOwner(final Player player) {
        return owner.getUniqueId().equals(player.getUniqueId());
    }

    // --------------------------------------------------------------------------------------------------------------
    // Static access
    // --------------------------------------------------------------------------------------------------------------


    /**
     * Create an uuid to identify the bound by verifying that there is no identical one already present
     *
     * @return the BaseAccount's uuid
     */
    public static UUID createAccountUUID() {
        UUID uuid = UUID.randomUUID();
        while (getAccountUUIDs().contains(uuid)) {
            uuid = UUID.randomUUID();
        }
        return uuid;
    }

    /**
     * Get the BaseAccount by its uuid
     *
     * @param uuid the uuid of account
     */
    public static BaseAccount findByAccount(final UUID uuid) {
        for (final BaseAccount count : getAccounts()) {
            if (count.getUuid().equals(uuid)) return count;
        }
        return null;
    }

    /**
     * Get the BaseAccount by its countNumber
     *
     * @param countNumber the count number of account
     */
    public static BaseAccount findByAccount(final int countNumber) {
        for (final BaseAccount count : getAccounts()) {
            if (count.getCountNumber() == countNumber) return count;
        }
        return null;
    }


    /**
     * Get all count of a player
     *
     * @param player the player
     */
    public static Set<BaseAccount> getPlayerCounts(final Player player) {
        final Set<BaseAccount> counts = new HashSet<>();
        for (final BaseAccount count : getAccounts()) {
            if (count.getOwner() == player || count.getTrusted() == player) counts.add(count);
        }
        return counts;
    }

    /**
     * Get a set count by his countNumbers
     *
     * @param countNumbers the numbers
     * @return
     */
    public static List<BaseAccount> getCountsByNumbers(final Set<Integer> countNumbers) {
        return Common.convert(countNumbers, integer -> {
            for (final BaseAccount count : getAccounts()) {
                if (count.getCountNumber() == integer) return count;
            }
            return null;
        });
    }


    /**
     * Return all BaseAccount
     */
    public static Collection<BaseAccount> getAccounts() {
        return Collections.unmodifiableCollection(byUUID.values());
    }

    /**
     * Return all name of BaseAccount
     *
     * @return a collection of players
     */
    public static Collection<UUID> getAccountUUIDs() {
        return Collections.unmodifiableCollection(byUUID.keySet());
    }

    // --------------------------------------------------------------------------------------------------------------
    // Serialization methods
    // --------------------------------------------------------------------------------------------------------------

    @Override
    public SerializedMap serialize() {
        final SerializedMap map = new SerializedMap();

        map.put("UUID", uuid);
        map.put("Count_Number", countNumber);
        map.put("Owner", owner.getUniqueId());

        if (trusted != null) map.put("Trusted", trusted.getUniqueId());

        map.put("Balance", balance);


        if (loans != null) map.put("Loans", loans);
        if (type != null) map.put("Type", type);

        map.put("Transactions", transactions);
        return map;
    }

    @SneakyThrows
    public static BaseAccount deserialize(final SerializedMap map) {

        final UUID uuid = map.getUUID("UUID");
        final int countNumber = map.getInteger("Count_Number");
        final OfflinePlayer owner = Remain.getOfflinePlayerByUUID(map.getUUID("Owner"));

        OfflinePlayer trusted = null;
        if (map.getUUID("Trusted") != null)
            trusted = Remain.getOfflinePlayerByUUID(map.getUUID("Trusted"));

        final int balance = map.getInteger("Balance");

        final AccountType type = AccountType.fromName(map.getString("Type"));
        final Set<Loan> loans = map.getSet("Loans", Loan.class);


        final Set<Transaction> transactions = map.getSet("Transactions", Transaction.class);


        return switch (type) {
            case PERSONAL -> new PersonalAccount(uuid, countNumber, owner, trusted, balance, loans, type, transactions);

            case BUSINESS -> new BusinessAccount(uuid, countNumber, owner, trusted, balance, loans, type, transactions);
        };
    }

}
