package me.spiderdeluxe.mycteriaeconomy.models.bank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.models.account.BaseAccount;
import me.spiderdeluxe.mycteriaeconomy.models.bank.transaction.TransactionType;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import me.spiderdeluxe.mycteriaeconomy.util.TimeUtility;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.collection.expiringmap.ExpiringMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.model.SimpleComponent;
import org.mineacademy.fo.remain.Remain;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This class manages all the Loans.
 */
@AllArgsConstructor
@Getter
public class Loan implements ConfigSerializable {
    /**
     * Stores request of loan, so they are singletons
     */
    private static final ExpiringMap<Player, LoanPropose> cacheMap = ExpiringMap.builder().expiration(120, TimeUnit.SECONDS).build();


    /**
     * Stores active loan by their uuid, so they are singletons
     */
    private static final Map<UUID, Loan> byUUID = new HashMap<>();

    /**
     * UUID of loan
     */
    private UUID uuid;

    /**
     * The person making the loan
     */
    private final BaseAccount lenderCount;
    /**
     * The person to whom the money is lent
     */
    private BaseAccount borrowerCount;

    /**
     * The money that is lent
     */
    private int loanedMoney;

    /**
     * Money left to repay including interest
     */
    private int payBackMoney;

    /**
     * The date of last Installment paid
     */
    private long lastInstallment;

    /* ------------------------------------------------------------------------------- */
    /* Loan manipulation methods */
    /* ------------------------------------------------------------------------------- */
    public static void createLend(final BaseAccount lenderCount, final BaseAccount borrowerCount, final int loanedMoney, final Location location) {
        final UUID uuid = createAccountUUID();

        if (lenderCount.getBalance() < loanedMoney)
            return;

        Messenger.success(borrowerCount.getOwner(), "You just took out a loan from " + lenderCount.getOwner().getName());
        Messenger.success(lenderCount.getOwner(), "You just made a loan to " + borrowerCount.getOwner().getName());

        final Loan loan = new Loan(uuid, lenderCount, borrowerCount, loanedMoney, (loanedMoney + (2 * loanedMoney / 100)), TimeUtil.currentTimeSeconds());

        byUUID.put(uuid, loan);
        borrowerCount.addLoan(loan);

        lenderCount.payAccount(TransactionType.LOAN, loanedMoney, borrowerCount, location);
    }

    public void deleteLend() {
        Messenger.success(borrowerCount.getOwner(), "You just paid your debt to " + lenderCount.getOwner().getName());

        if (lenderCount.getOwner().isOnline())
            Messenger.success(lenderCount.getOwner(), borrowerCount.getOwner().getName() + "He just paid his debt to you.");

        borrowerCount.removeLoan(this);
        byUUID.remove(uuid, this);
    }
    /* ------------------------------------------------------------------------------- */
    /* Loan request methods */
    /* ------------------------------------------------------------------------------- */

    public static void offerLend(final BaseAccount lenderCount, final BaseAccount borrowerCount, final int loanedMoney) {
        cacheMap.put(borrowerCount.getOwner(), new LoanPropose(lenderCount, borrowerCount, loanedMoney));
        SimpleComponent
                .of(lenderCount.getOwner().getName() + "&7 is offering you a loan of &c" + loanedMoney + "&7, do you ")
                .append("&baccept ")
                .onHover("&7Click to accept offer")
                .onClickRunCmd("/loan accept " + lenderCount.getOwner().getName())
                .append("&7this offer or ")
                .append("&cdecline ")
                .onHover("&7Click to decline offer")
                .onClickRunCmd("/loan decline " + lenderCount.getOwner().getName())
                .append("&7it.")
                .send(borrowerCount.getOwner());

    }


    /**
     * Retire a lend propose
     *
     * @param lender the lender
     */
    public static void retireLendPropose(final Player lender) {
        for (final LoanPropose loanPropose : cacheMap.values()) {
            if (loanPropose.lenderCount.getOwner() == lender) {
                Messenger.success(lender, "You just cancelled your loan proposal to " + loanPropose.borrowerCount.getOwner().getName());
                cacheMap.remove(loanPropose.borrowerCount.getOwner(), loanPropose);
                return;
            }
            Messenger.error(lender, "You haven't made any loan proposals.");
        }

    }

    /**
     * Decline a lend propose
     *
     * @param borrower the borrower
     */
    public static void declineLendPropose(final Player borrower) {
        for (final LoanPropose loanPropose : cacheMap.values()) {
            if (loanPropose.borrowerCount.getOwner() == borrower) {
                Messenger.success(borrower, "You have just returned the loan proposal of " + loanPropose.lenderCount.getOwner().getName());
                cacheMap.remove(loanPropose.borrowerCount.getOwner(), loanPropose);
                return;
            }
            Messenger.error(borrower, "You haven't active any loan proposals.");
        }

    }

    /**
     * Accept a lend propose
     *
     * @param borrower the borrower
     */
    public static void acceptLendPropose(final Player borrower) {
        for (final LoanPropose loanPropose : cacheMap.values()) {
            if (loanPropose.borrowerCount.getOwner() == borrower) {
                Loan.createLend(loanPropose.lenderCount, loanPropose.borrowerCount, loanPropose.amount, borrower.getLocation());
                cacheMap.remove(loanPropose.borrowerCount.getOwner(), loanPropose);
                return;
            }
            Messenger.error(borrower, "You haven't active any loan proposals.");
        }

    }

    /* ------------------------------------------------------------------------------- */
    /* Managing methods */
    /* ------------------------------------------------------------------------------- */


    /**
     * Pay part of debt, if total the debt will be cancelled
     *
     * @param amount the amount
     */
    public void payLoan(final int amount, final Location location) {
        Valid.checkBoolean(borrowerCount.getBalance() >= amount, "The player cannot pay more than his balance (his balance is: " + borrowerCount.getBalance() + ")");
        Valid.checkBoolean(payBackMoney >= amount, "You cannot pay more than your remained debt (your remained debt is: " + payBackMoney + "/" + loanedMoney + ")");

        borrowerCount.payAccount(TransactionType.BILL, loanedMoney, lenderCount, location);

        this.payBackMoney = payBackMoney - amount;

        if (payBackMoney == 0) {
            deleteLend();
            return;
        }

        lastInstallment = TimeUtil.currentTimeSeconds();

        Messenger.success(borrowerCount.getOwner(), "You just paid $" + amount + " of the $" + (payBackMoney + amount) + " you owe to Spider. " + this.getLenderCount().getOwner());


        final Player owner = lenderCount.getOwner();

        if (owner.isOnline())
            Messenger.success(owner, "You were paid$" + amount + " of the $" + (payBackMoney + amount) + " " + this.getLenderCount().getOwner() + " owes you.");

    }

    /**
     * Take installment from borrower's account.
     */
    public void addPenalty() {
        final long pastTime = TimeUtil.currentTimeSeconds() - lastInstallment;
        if (pastTime > Settings.General.LOAN_TIME.getTimeSeconds()) {

            final int penalty = (Settings.General.LOAN_PENALTY_PERCENTAGE * loanedMoney) / 100;

            loanedMoney = loanedMoney + penalty;

            if (payBackMoney == 0)
                return;

            lastInstallment = TimeUtil.currentTimeSeconds();

            Messenger.warn(borrowerCount.getOwner(), "Because you have not paid some of your debts  to " + getBorrowerCount().getOwner().getName() + ",  has been added a penalty of " + penalty + " (10% of " + loanedMoney + ")");
        }
    }

    public static void checkPlayerInstallment() {
        for (final Player player : Remain.getOnlinePlayers()) {
            for (final Loan loan : getBorrowerLoans(player))
                loan.addPenalty();
        }
    }


    /* ------------------------------------------------------------------------------- */
    /* Utilities methods */
    /* ------------------------------------------------------------------------------- */


    /**
     * Check if a borrower has already a lend request.
     *
     * @param borrower the borrower
     */
    public static boolean hasLendRequest(final Player borrower) {
        return cacheMap.containsKey(borrower);
    }


    /**
     * Check if a player is borrower
     *
     * @param borrower the borrower
     */
    public static boolean isBorrower(final Player borrower) {
        for (final Loan loan : getLoans()) {
            if (loan.getBorrowerCount().getOwner().equals(borrower))
                return true;
        }
        return false;
    }

    /**
     * Check if a player is lender
     *
     * @param lender the lender
     */
    public static boolean isLender(final Player lender) {
        for (final Loan loan : getLoans()) {
            if (loan.getLenderCount().getOwner().equals(lender))
                return true;
        }
        return false;
    }

    /**
     * Check if the lender has already made a lend to a borrower.
     *
     * @param lender   the lender
     * @param borrower the borrower
     */
    public static boolean hasLend(final Player lender, final Player borrower) {
        return findByPlayers(lender, borrower) != null;
    }


    /**
     * Check if the lender has already made a lend to a borrower.
     *
     * @param lenderCount   the lender
     * @param borrowerCount the borrower
     */
    public static boolean hasLend(final BaseAccount lenderCount, final BaseAccount borrowerCount) {
        return findByAccounts(lenderCount, borrowerCount) != null;
    }


    /**
     * Create an uuid to identify the loan by verifying that there is no identical one already present
     *
     * @return the Loan's uuid
     */
    public static UUID createAccountUUID() {
        UUID uuid = UUID.randomUUID();
        while (getLoansUUID().contains(uuid)) {
            uuid = UUID.randomUUID();
        }
        return uuid;
    }


    /**
     * Get the Loan by  its lender and borrower
     *
     * @param lender   the lender
     * @param borrower the borrower
     */
    public static Loan findByPlayers(final Player lender, final Player borrower) {
        for (final Loan loan : getLoans()) {
            if (loan.getLenderCount().getOwner().equals(lender)
                    && loan.getBorrowerCount().getOwner().equals(borrower))
                return loan;
        }
        return null;
    }


    /**
     * Get the Loan by  its lender and borrower
     *
     * @param lenderCount   the count of lender
     * @param borrowerCount the count of borrower
     */
    public static Loan findByAccounts(final BaseAccount lenderCount, final BaseAccount borrowerCount) {
        for (final Loan loan : getLoans()) {
            if (loan.getLenderCount().equals(lenderCount)
                    && loan.getBorrowerCount().equals(borrowerCount))
                return loan;
        }
        return null;
    }

    /**
     * Obtain all loan of a borrower
     *
     * @param player the player
     */
    public static List<Loan> getBorrowerLoans(final Player player) {
        return Common.convert(getLoans(), loan -> {
            if (loan.getBorrowerCount().getOwner().equals(player))
                return loan;

            return null;
        });
    }

    /**
     * Obtain all loan of a lender
     *
     * @param player the player
     */
    public static List<Loan> getLenderLoans(final Player player) {
        return Common.convert(getLoans(), loan -> {
            if (loan.getLenderCount().getOwner().equals(player))
                return loan;

            return null;
        });
    }


    /**
     * Return all Loans
     */
    public static Collection<Loan> getLoans() {
        return Collections.unmodifiableCollection(byUUID.values());
    }

    /**
     * Return all uuid of Loans
     *
     * @return a collection of players
     */
    public static Collection<UUID> getLoansUUID() {
        return Collections.unmodifiableCollection(byUUID.keySet());
    }


    /* ------------------------------------------------------------------------------- */
    /* Serialization methods */
    /* ------------------------------------------------------------------------------- */

    @Override
    public SerializedMap serialize() {
        final SerializedMap map = new SerializedMap();

        map.put("UUID", uuid);
        map.put("Lender_Count", lenderCount.getCountNumber());
        map.put("Borrower", borrowerCount.getOwner().getUniqueId());
        map.put("Borrower_Count", borrowerCount.getCountNumber());
        map.put("Loaned_Money", loanedMoney);
        map.put("PayBack_Money", payBackMoney);
        map.put("Last_Installment", TimeUtil.getFormattedDate((int) lastInstallment * 1000L));
        return map;
    }

    @SneakyThrows
    public static Loan deserialize(final SerializedMap map) {

        final UUID uuid = map.getUUID("UUID");

        final BaseAccount lenderCount = BaseAccount.findByAccount(map.getInteger("Lender_Count"));


        EconomyPlayer.from(map.getUUID("Borrower"));
        final BaseAccount borrowerCount = BaseAccount.findByAccount(map.getInteger("Borrower_Count"));


        if (lenderCount == null || borrowerCount == null) return null;

        final int loanedMoney = map.getInteger("Loaned_Money");
        final int payBackMoney = map.getInteger("PayBack_Money");

        final long lastInstallment = TimeUtility.convertTime(map.getString("Last_Installment")) / 1000;


        return new Loan(uuid, lenderCount, borrowerCount, loanedMoney, payBackMoney, lastInstallment);
    }


    @Data
    @AllArgsConstructor
    private static class LoanPropose {
        private BaseAccount lenderCount;
        private BaseAccount borrowerCount;
        private int amount;
    }
}
