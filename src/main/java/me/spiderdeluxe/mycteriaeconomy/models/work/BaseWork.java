package me.spiderdeluxe.mycteriaeconomy.models.work;


import lombok.*;
import me.spiderdeluxe.mycteriaeconomy.cache.DataStorage;
import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.models.account.BaseAccount;
import me.spiderdeluxe.mycteriaeconomy.models.account.PersonalAccount;
import me.spiderdeluxe.mycteriaeconomy.models.bank.transaction.TransactionType;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import me.spiderdeluxe.mycteriaeconomy.util.TimeUtility;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.collection.expiringmap.ExpiringMap;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.model.SimpleComponent;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.Remain;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
public class BaseWork implements ConfigSerializable {

    /**
     * Stores active work by their uuid, so they are singletons
     */
    private static final Map<UUID, BaseWork> byUUID = new HashMap<>();

    /**
     * Stores request of work, so they are singletons
     */
    private static final ExpiringMap<Player, BaseWork.WorkPropose> cacheMap = ExpiringMap.builder().expiration(120, TimeUnit.SECONDS).build();


    @Setter
    /**
     * The name of work
     */
    private String name;


    /**
     * The unique uuid of work.
     */
    private final UUID uuid;

    /**
     * The administrator of work
     */
    private final OfflinePlayer administrator;

    /**
     * The employees of work
     */
    private Set<Employer> employees = new HashSet<>();


    private final int salary = 60;

    public BaseWork(final UUID uuid, final String name, final OfflinePlayer administrator, final Set<Employer> employees) {
        this.uuid = uuid;
        this.name = name;
        this.administrator = administrator;
        this.employees = employees;

        createWork();
    }

    public BaseWork(final OfflinePlayer administrator) {
        this.uuid = createWorkUUIDs();
        this.administrator = administrator;

        createWork();
    }

    /* ------------------------------------------------------------------------------- */
    /* Transaction Manipulation */
    /* ------------------------------------------------------------------------------- */

    public void createWork() {
        final DataStorage dataStorage = DataStorage.getInstance();
        dataStorage.addWork(this);

        byUUID.put(uuid, this);
    }

    public void deleteWork() {
        final DataStorage dataStorage = DataStorage.getInstance();
        dataStorage.removeWork(this);

        byUUID.remove(this.getUuid(), this);
    }


    public void addEmployer(final Player player) {
        employees.add(new Employer(player, TimeUtil.currentTimeSeconds()));
    }

    public void removeEmployer(final Player player) {
        employees.remove(findEmployer(player));
    }


    public Employer findEmployer(final Player player) {
        for (final Employer employer : employees)
            if (employer.employer == player) return employer;
        return null;
    }

    /* ------------------------------------------------------------------------------- */
    /* Employer Manipulation */
    /* ------------------------------------------------------------------------------- */


    /**
     * Assume player
     *
     * @param employer the employer to assume
     */
    public void invitePlayer(final Player employer) {
        cacheMap.put(employer, new WorkPropose(administrator.getPlayer(), employer));
        SimpleComponent
                .of(administrator.getName() + "&7 is offering you a job in &c" + name + "&7, do you ")
                .append("&baccept")
                .onHover(" &7Click to accept offer ")
                .onClickRunCmd("/work accept ")
                .append("&7this offer or ")
                .append("&cdecline")
                .onHover(" &7Click to decline offer ")
                .onClickRunCmd("/work decline ")
                .append("&7it.")
                .send(administrator.getPlayer());

    }


    /**
     * Check if an employer has already sent a work request
     *
     * @param employer the administrator
     */
    public static boolean hasInvite(final Player employer) {
        return cacheMap.containsKey(employer);
    }


    /**
     * Retire a work invite
     */
    public void retireInvite() {
        for (final WorkPropose workPropose : cacheMap.values()) {
            if (workPropose.administrator == administrator) {
                Messenger.success(administrator.getPlayer(), "You just cancelled your job proposal to " + workPropose.employer);
                cacheMap.remove(workPropose.employer, workPropose);
                return;
            }
            Messenger.error(administrator.getPlayer(), "You haven't made any loan proposals.");
        }

    }

    /**
     * Decline a work propose
     *
     * @param employer the employer
     */
    public static void declineInvite(final Player employer) {
        for (final WorkPropose workPropose : cacheMap.values()) {
            if (workPropose.employer == employer) {
                Messenger.success(employer, "You have just returned the job proposal of " + workPropose.administrator.getName());
                cacheMap.remove(workPropose.employer, workPropose);
                return;
            }
            Messenger.error(employer, "You haven't active any job proposals.");
        }

    }

    /**
     * Accept a work propose
     *
     * @param employer the employer
     */
    public static void acceptInvite(final Player employer) {
        for (final WorkPropose workPropose : cacheMap.values()) {
            if (workPropose.employer == employer) {
                final BaseWork work = BaseWork.fromAdministrator(workPropose.administrator);
                work.addEmployer(employer);
                Messenger.success(employer, "You have been successfully hired in the company of  " + workPropose.administrator.getName());
                cacheMap.remove(employer, workPropose);
                return;
            }
            Messenger.error(employer, "You haven't active any job proposals.");
        }
    }

    /* ------------------------------------------------------------------------------- */
    /* Salary Manipulation */
    /* ------------------------------------------------------------------------------- */

    /**
     * Pay salary of an employer
     *
     * @param account  the account
     * @param employer the employer
     */
    public void payEmployer(final BaseAccount account, final Employer employer) {
        final EconomyPlayer employerEco = EconomyPlayer.from(employer.employer);
        final PersonalAccount employerCount = employerEco.getPersonalAccount();

        if (employer.getEmployer().isOnline()) {
            Messenger.success(employer.employer.getPlayer(), "You were paid of your work's salary (" + salary + ")");
            account.decreaseBalance(salary);

            final Inventory inventory = Objects.requireNonNull(employer.employer.getPlayer()).getInventory();
            inventory.addItem(employer.receipt(salary));
        } else {
            account.payAccount(TransactionType.PAYMENT, salary, employerCount, null);
        }

        employer.lastSalary = TimeUtil.currentTimeSeconds();
    }


    /**
     * Add penalty to the unpaved administrator
     */
    public void payAuto(final Employer employer) {
        final long pastTime = TimeUtil.currentTimeSeconds() - employer.lastSalary;
        if (pastTime > Settings.General.LOAN_TIME.getTimeSeconds()) {
            final EconomyPlayer economyPlayer = EconomyPlayer.from(administrator);

            final BaseAccount account = economyPlayer.getAccountWithEnoughMoney(salary);
            payEmployer(account, employer);
        }
    }

    public static void checkSalaryPayment() {
        for (final BaseWork work : getWorks())
            for (final Employer employer : work.employees)
                work.payAuto(employer);
    }


    /* ------------------------------------------------------------------------------- */
    /* Static methods */
    /* ------------------------------------------------------------------------------- */

    /**
     * Create an uuid to identify the work by verifying that there is no identical one already present
     *
     * @return the Work's uuid
     */
    public static UUID createWorkUUIDs() {
        UUID uuid = UUID.randomUUID();

        while (getWorksUUID().contains(uuid)) {
            uuid = UUID.randomUUID();
        }
        return uuid;
    }


    /**
     * Check if a player has a work
     *
     * @param player the player
     */
    public static boolean hasWork(final Player player) {
        return fromEmployer(player) != null || fromAdministrator(player) != null;
    }

    /**
     * Get the BaseWork by an administrator
     *
     * @param player the player
     */
    public static BaseWork fromAdministrator(final Player player) {
        for (final BaseWork work : getWorks()) {
            if (work.administrator == player) return work;
        }
        return null;
    }


    /**
     * Get the BaseWork by an employer
     *
     * @param player the player
     */
    public static BaseWork fromEmployer(final Player player) {
        for (final BaseWork work : getWorks()) {
            if (work.findEmployer(player) != null) return work;
        }
        return null;
    }


    /**
     * Return all BaseWorks
     */
    public static Collection<BaseWork> getWorks() {
        return Collections.unmodifiableCollection(byUUID.values());
    }


    /**
     * Return all uuid of BaseWorks
     */
    public static Collection<UUID> getWorksUUID() {

        return Collections.unmodifiableCollection(byUUID.keySet());
    }

    // --------------------------------------------------------------------------------------------------------------
    // Serialization method
    // --------------------------------------------------------------------------------------------------------------


    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray(
                "UUID", uuid,
                "Name", name,
                "Administrator", administrator.getUniqueId(),
                "Employees", employees);
    }

    @SneakyThrows
    public static BaseWork deserialize(final SerializedMap map) {
        final UUID uuid = map.getUUID("UUID");

        final String name = map.getString("Name");

        final OfflinePlayer administrator = Remain.getOfflinePlayerByUUID(map.getUUID("Administrator"));

        final Set<Employer> employees = map.getSet("Employees", Employer.class);

        return new BaseWork(uuid, name, administrator, employees);
    }


    @Data
    @AllArgsConstructor
    private static class WorkPropose {
        private Player administrator;
        private Player employer;
    }

    @Data
    @AllArgsConstructor
    private static class Employer implements ConfigSerializable {
        private OfflinePlayer employer;
        private long lastSalary;


        public ItemStack receipt(final int amount) {
            final ItemStack item = ItemCreator.of(CompMaterial.PAPER)
                    .name("&7" + employer.getName() + "'s salary")
                    .lores(List.of(
                            "Employer: " + employer.getName(),
                            "Date: " + TimeUtil.currentTimeSeconds(),
                            "Salary: " + amount))
                    .build().make();

            CompMetadata.setMetadata(item, "EMPLOYER_NAME", employer.getName());
            CompMetadata.setMetadata(item, "EMPLOYER_SALARY", "" + amount);

            return item;
        }

        @Override
        public SerializedMap serialize() {
            return SerializedMap.ofArray(
                    "Employer", employer.getUniqueId(),
                    "Last_Salary", TimeUtil.getFormattedDate(lastSalary * 1000));
        }

        @SneakyThrows
        public static Employer deserialize(final SerializedMap map) {
            final OfflinePlayer employer = Remain.getOfflinePlayerByUUID(map.getUUID("Administrator"));
            final long lastSalary = TimeUtility.convertTime(map.getString("Last_Salary")) / 1000;

            return new Employer(employer, lastSalary);
        }
    }

}
