package me.spiderdeluxe.mycteriaeconomy.models.bank;

import java.util.Set;
import java.util.UUID;

/**
 * This class manages the state banks of the server
 *
 * @author SpiderDeluxe
 */

public class StateBank extends BaseBank {
    

    public StateBank(final UUID uuid, final int balance, final Set<Branch> branches) {
        super(BankType.STATE, uuid, balance, branches);
    }

    public StateBank() {
        super(BankType.STATE);
    }


}
