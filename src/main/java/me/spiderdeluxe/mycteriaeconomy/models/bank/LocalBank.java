package me.spiderdeluxe.mycteriaeconomy.models.bank;

import java.util.Set;
import java.util.UUID;

/**
 * This class manages the local banks of the server
 *
 * @author SpiderDeluxe
 */
public class LocalBank extends BaseBank {
    

    public LocalBank(final UUID uuid, final int balance, final Set<Branch> branches) {
        super(BankType.LOCAL, uuid, balance, branches);
    }

    public LocalBank() {
        super(BankType.LOCAL);
    }


}
