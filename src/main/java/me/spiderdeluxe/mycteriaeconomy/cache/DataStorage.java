package me.spiderdeluxe.mycteriaeconomy.cache;

import lombok.Getter;
import lombok.Setter;
import me.spiderdeluxe.mycteriaeconomy.models.bank.BaseBank;
import me.spiderdeluxe.mycteriaeconomy.models.bank.LocalBank;
import me.spiderdeluxe.mycteriaeconomy.models.bank.StateBank;
import me.spiderdeluxe.mycteriaeconomy.models.work.BaseWork;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.HashSet;
import java.util.Set;

public class DataStorage extends YamlConfig {


    @Getter
    private static final DataStorage instance = new DataStorage();

    @Getter
    private Set<BaseBank> activeBanks = new HashSet<>();


    private Set<BaseWork> activeWorks = new HashSet<>();

    @Setter
    @Getter
    private double nationalFounds;

    @Override
    protected void onLoadFinish() {

        if (isSet("Bank")) {
            activeBanks = getSet("Bank", BaseBank.class);
        }

        if (isSet("Work")) {
            activeWorks = getSet("Work", BaseWork.class);
        }

        if (isSet("National Founds")) {
            nationalFounds = getDouble("National Founds");
        }
    }

    public void load() {
        loadConfiguration(null, "data.db");
    }


    public void saveData() {
        if (isSet("Bank")) {
            save("Bank", activeBanks);
        }

        if (isSet("Work")) {
            save("Work", activeWorks);
        }

        if (isSet("National Founds")) {
            save("National Founds", nationalFounds);
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    // Banks manipulation
    // --------------------------------------------------------------------------------------------------------------

    public void addBank(final BaseBank baseBank) {
        Valid.checkNotNull(baseBank, "This bank doesn't exists");


        activeBanks.add(baseBank);
        save("Bank", activeBanks);
    }

    public void removeBank(final BaseBank baseBank) {

        activeBanks.remove(baseBank);
        save("Bank", activeBanks);
    }


    // --------------------------------------------------------------------------------------------------------------
    // Banks manipulation
    // --------------------------------------------------------------------------------------------------------------

    public void addWork(final BaseWork work) {
        Valid.checkNotNull(work, "This bank doesn't exists");


        activeWorks.add(work);
        save("Bank", activeWorks);
    }

    public void removeWork(final BaseWork work) {

        activeWorks.remove(work);
        save("Bank", activeWorks);
    }


    /**
     * Obtain the main state Bank
     */
    public StateBank getStateBank() {
        for (final BaseBank bank : getActiveBanks())
            if (bank instanceof StateBank)
                return (StateBank) bank;
        return new StateBank();
    }

    /**
     * Obtain the main local Bank
     */
    public LocalBank getLocalBank() {
        for (final BaseBank bank : getActiveBanks())
            if (bank instanceof LocalBank)
                return (LocalBank) bank;
        return new LocalBank();
    }


    // --------------------------------------------------------------------------------------------------------------
    // National Founds manipulation
    // --------------------------------------------------------------------------------------------------------------

    public void increaseFounds(final double amount) {

        nationalFounds += amount;
        save("National Founds", nationalFounds);

    }

    public void decreaseFounds(final double amount) {

        nationalFounds -= amount;
        save("National Founds", nationalFounds);
    }

}
