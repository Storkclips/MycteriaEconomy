package me.spiderdeluxe.mycteriaeconomy.models.account;

import me.spiderdeluxe.mycteriaeconomy.models.bank.Loan;
import me.spiderdeluxe.mycteriaeconomy.models.bank.transaction.Transaction;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

/**
 * This is the personalAccount of the player
 *
 * @author SpiderDeluxe
 */
public class BusinessAccount extends BaseAccount {


    public BusinessAccount(final UUID uuid, final Player owner) {
        super(uuid, owner);


        setType(AccountType.BUSINESS);

        createAccount(true);
    }

    public BusinessAccount(final Player owner) {
        super(owner);

        setType(AccountType.BUSINESS);

        createAccount(true);
    }

    public BusinessAccount(final UUID uuid, final int countNumber, final OfflinePlayer owner, final OfflinePlayer trusted, final int balance, final Set<Loan> loans, final AccountType accountType, final Set<Transaction> transaction) {
        super(uuid, countNumber, owner, trusted, balance, loans, accountType, transaction);

        setType(AccountType.BUSINESS);

        createAccount(false);
    }

    // --------------------------------------------------------------------------------------------------------------
    // Count Manipulation
    // --------------------------------------------------------------------------------------------------------------


    // --------------------------------------------------------------------------------------------------------------
    // Balance Manipulation
    // --------------------------------------------------------------------------------------------------------------


    @Override
    public int getCommission() {
        return Settings.General.BUSINESS_COMMISSION_FEE_PERCENTAGE;
    }
    

}
