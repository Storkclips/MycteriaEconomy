package me.spiderdeluxe.mycteriaeconomy.models.account;

import me.spiderdeluxe.mycteriaeconomy.models.bank.Loan;
import me.spiderdeluxe.mycteriaeconomy.models.bank.transaction.Transaction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

/**
 * This is the personalAccount of the player
 *
 * @author SpiderDeluxe
 */
public class PersonalAccount extends BaseAccount {


    public PersonalAccount(final UUID uuid, final Player owner) {
        super(uuid, owner);


        setType(AccountType.PERSONAL);

        createAccount(true);
    }

    public PersonalAccount(final Player owner) {
        super(owner);

        setType(AccountType.PERSONAL);

        createAccount(true);
    }

    public PersonalAccount(final UUID uuid, final int countNumber, final OfflinePlayer owner, final OfflinePlayer trusted, final int balance, final Set<Loan> loans, final AccountType accountType, final Set<Transaction> transaction) {
        super(uuid, countNumber, owner, trusted, balance, loans, accountType, transaction);

        setType(AccountType.PERSONAL);

        createAccount(false);
    }


}
