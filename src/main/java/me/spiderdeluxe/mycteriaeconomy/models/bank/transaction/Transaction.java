package me.spiderdeluxe.mycteriaeconomy.models.bank.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import me.spiderdeluxe.mycteriaeconomy.models.account.BaseAccount;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import me.spiderdeluxe.mycteriaeconomy.util.TimeUtility;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;

import java.util.*;

@Data
@AllArgsConstructor
public class Transaction implements ConfigSerializable {

    /**
     * Stores transaction by their uuid, so they are singletons
     */
    private static final Map<UUID, Transaction> byUUID = new HashMap<>();

    /**
     * The unique uuid of transaction
     */
    private UUID uuid;

    /**
     * The type of transaction that the player can do
     */
    private TransactionType type;


    /**
     * When payment has been made
     */
    private long time;

    /**
     * Amount of money that is spent
     */
    private double amount;

    /**
     * Who is the payer
     */
    private int payer;

    /**
     * Who gets paid
     */
    private int paid;


    /* ------------------------------------------------------------------------------- */
    /* Transaction Manipulation */
    /* ------------------------------------------------------------------------------- */

    public static void createTransaction(final TransactionType type, final double amount, final BaseAccount payer, final BaseAccount paid) {
        final UUID uuid = createTransactionUUID();

        final Transaction transaction = new Transaction(uuid, type, TimeUtil.currentTimeSeconds(), amount, payer.getCountNumber(), paid != null ? paid.getCountNumber() : 0);

        payer.addTransaction(transaction);
        byUUID.put(uuid, transaction);
    }

    public void deleteTransaction() {
        if (getPayer() != null) getPayer().removeTransaction(this);
        byUUID.remove(uuid, this);
    }

    /* ------------------------------------------------------------------------------- */
    /* Clean Transaction */
    /* ------------------------------------------------------------------------------- */


    /**
     * Clear transaction of an account if a week has passed since it was made
     */
    public void clearTransaction() {
        final long pastTime = TimeUtil.currentTimeSeconds() - time;
        if (pastTime > Settings.General.TRANSACTION_TIME.getTimeSeconds()) {
            deleteTransaction();
        }
    }

    public static void clearPlayerTransaction() {
        for (final Transaction transaction : getTransactions()) {
            transaction.clearTransaction();
        }
    }


    /* ------------------------------------------------------------------------------- */
    /* Static methods */
    /* ------------------------------------------------------------------------------- */

    public BaseAccount getPayer() {
        return BaseAccount.findByAccount(payer);
    }


    public BaseAccount getPaid() {
        return BaseAccount.findByAccount(paid);
    }



    /* ------------------------------------------------------------------------------- */
    /* Static methods */
    /* ------------------------------------------------------------------------------- */

    /**
     * Create an uuid to identify the transaction by verifying that there is no identical one already present
     *
     * @return the Transaction's uuid
     */
    public static UUID createTransactionUUID() {
        UUID uuid = UUID.randomUUID();

        while (getTransactionsUUID().contains(uuid)) {
            uuid = UUID.randomUUID();
        }
        return uuid;
    }


    /**
     * Obtain transaction of account including payer transaction.
     *
     * @param account the account
     * @return
     */
    public static Set<Transaction> getAccountTransaction(final BaseAccount account) {
        final Set<Transaction> transactions = new HashSet<>();
        for (final Transaction transaction : getTransactions()) {
            if ((transaction.getPayer() != null && transaction.getPayer().equals(account)) ||
                    (transaction.getPaid() != null && transaction.getPaid().equals(account)))
                transactions.add(transaction);
        }
        return transactions;
    }


    /**
     * Return all Transactions
     */
    public static Collection<Transaction> getTransactions() {
        return Collections.unmodifiableCollection(byUUID.values());
    }


    /**
     * Return all uuid of Transactions
     */
    public static Collection<UUID> getTransactionsUUID() {

        return Collections.unmodifiableCollection(byUUID.keySet());
    }



    /* ------------------------------------------------------------------------------- */
    /* Serialization methods */
    /* ------------------------------------------------------------------------------- */


    @Override
    public SerializedMap serialize() {
        final SerializedMap map = new SerializedMap();
        map.put("UUID", uuid);
        map.put("Type", type);
        map.put("Time", TimeUtil.getFormattedDate(time * 1000));
        map.put("Amount", amount);
        map.put("Payer", payer);
        if (paid != 0) map.put("Paid", paid);
        return map;
    }


    @SneakyThrows
    public static Transaction deserialize(final SerializedMap map) {
        final UUID uuid = map.getUUID("UUID");
        final TransactionType type = TransactionType.fromName(map.getString("Type"));
        final long time = TimeUtility.convertTime(map.getString("Time")) / 1000;
        final double amount = map.getDouble("Amount");
        final int payer = map.getInteger("Payer");

        if (map.containsKey("Paid")) {
            final int paid = map.getInteger("Paid");

            final Transaction transaction = new Transaction(uuid, type, time, amount, payer, paid);
            byUUID.put(uuid, transaction);
            return transaction;
        } else {
            final Transaction transaction = new Transaction(uuid, type, time, amount, payer, 0);
            byUUID.put(uuid, transaction);
            return transaction;
        }
    }

}
