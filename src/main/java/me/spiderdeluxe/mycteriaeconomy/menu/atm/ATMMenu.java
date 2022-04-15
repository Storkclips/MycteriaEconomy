package me.spiderdeluxe.mycteriaeconomy.menu.atm;

import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.conversation.ATMTransactionAmountPrompt;
import me.spiderdeluxe.mycteriaeconomy.models.account.AccountType;
import me.spiderdeluxe.mycteriaeconomy.models.account.BaseAccount;
import me.spiderdeluxe.mycteriaeconomy.models.bank.ATM;
import me.spiderdeluxe.mycteriaeconomy.models.bank.transaction.Transaction;
import me.spiderdeluxe.mycteriaeconomy.models.bank.transaction.TransactionType;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import me.spiderdeluxe.mycteriaeconomy.util.CurrencyItem;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.SimpleSound;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;

public class ATMMenu extends MenuATMPagged<BaseAccount> {


    EconomyPlayer economyPlayer;

    Button logButton;

    public ATMMenu(final EconomyPlayer economyPlayer) {
        super(economyPlayer.getAccountInOrder());
        setSize(3 * 9);

        setSound(new SimpleSound(Sound.valueOf(Settings.Menu.ATM_ACTIVATION_SOUND),
                Settings.Menu.ATM_ACTIVATION_VOLUME.floatValue(),
                Settings.Menu.ATM_ACTIVATION_PITCH.floatValue()));

        setTitle("&8Atm");
        addInfoButton();

        this.economyPlayer = economyPlayer;


        logButton = new Button() {
            @Override
            public void onClickedInMenu(final Player player, final Menu menu, final ClickType clickType) {
                new TransactionMenu(menu, economyPlayer).displayTo(player);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.PAPER)
                        .name("&7Transaction logs")
                        .lores(List.of("&bClick to see the summary of your transitions"))
                        .build().make();
            }
        };
    }


    @Override
    protected ItemStack convertToItemStack(final BaseAccount count) {
        if (count.getType() == AccountType.PERSONAL)
            return ItemCreator.of(CompMaterial.PAPER)
                    .name("&aPersonal Account")
                    .lores(List.of("&7Account Number: &f" + count.getCountNumber(),
                            "&7Balance: &f" + count.getBalance(),
                            "&7Owner: &f" + count.getOwner().getName()))
                    .build().make();
        else
            return ItemCreator.of(CompMaterial.PAPER)
                    .name("&aBusiness Account")
                    .lores(List.of("&7Account Number: &f" + count.getCountNumber(),
                            "&7Balance: &f" + count.getBalance(),
                            "&7Owner: &f" + count.getOwner().getName()))
                    .build().make();
    }

    @Override
    protected void onPageClick(final Player player, final BaseAccount count, final ClickType clickType) {
        new WalletMenu(this, count, false).displayTo(player);
    }


    @Override
    public Button formEmptyItemButton() {
        return new Button() {


            @Override
            public void onClickedInMenu(final Player player, final Menu menu, final ClickType click) {
            }

            @Override
            public ItemStack getItem() {

                return ItemCreator
                        .of(CompMaterial.RED_STAINED_GLASS_PANE)
                        .name("&cEmpty Account")
                        .lore("&4This bank account has not yet been unlocked.")
                        .build().make();
            }
        };
    }


    @Override
    public ItemStack getItemAt(final int slot) {
        if (slot == 16)
            return logButton.getItem();
        return super.getItemAt(slot);
    }

    @Override
    protected String[] getInfo() {
        return super.getInfo();
    }


    // --------------------------------------------------------------------------------------------------------------
    // Bank Account menus
    // --------------------------------------------------------------------------------------------------------------
    public static class WalletMenu extends Menu {

        BaseAccount baseAccount;
        boolean isDeposit;

        Button oneDollarButton;
        Button fiveDollarButton;
        Button tenDollarButton;
        Button twentyDollarButton;
        Button oneHundredButton;

        Button customAmountButton;
        Button logButton;

        public WalletMenu(final Menu parent, final BaseAccount baseAccount, final boolean isDeposit) {
            super(parent);
            setSize(9 * 3);
            setTitle(baseAccount.getType().getStringMessage() + " Account Menu");

            addReturnButton();
            addInfoButton();

            this.baseAccount = baseAccount;
            this.isDeposit = isDeposit;

            oneDollarButton = new Button() {
                @Override
                public void onClickedInMenu(final Player player, final Menu menu, final ClickType clickType) {
                    transaction(CurrencyItem.oneDollarBill());
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CurrencyItem.oneDollarBill())
                            .lores(List.of("&7" + transactionName() + " &c1 &7dollar."))
                            .build().make();
                }
            };

            fiveDollarButton = new Button() {
                @Override
                public void onClickedInMenu(final Player player, final Menu menu, final ClickType clickType) {
                    transaction(CurrencyItem.fiveDollarBill());

                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CurrencyItem.fiveDollarBill())
                            .lores(List.of("&7" + transactionName() + " &c5 &7dollars."))
                            .build().make();
                }
            };

            tenDollarButton = new Button() {
                @Override
                public void onClickedInMenu(final Player player, final Menu menu, final ClickType clickType) {
                    transaction(CurrencyItem.tenDollarBill());

                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CurrencyItem.tenDollarBill())
                            .lores(List.of("&7" + transactionName() + " &c10 &7dollars."))
                            .build().make();
                }
            };

            twentyDollarButton = new Button() {
                @Override
                public void onClickedInMenu(final Player player, final Menu menu, final ClickType clickType) {
                    transaction(CurrencyItem.twentyDollarBill());
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CurrencyItem.twentyDollarBill())
                            .lores(List.of("&7" + transactionName() + " &c20 &7dollars."))
                            .build().make();
                }
            };

            oneHundredButton = new Button() {
                @Override
                public void onClickedInMenu(final Player player, final Menu menu, final ClickType clickType) {
                    transaction(CurrencyItem.oneHundredDollarBill());
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CurrencyItem.oneHundredDollarBill())
                            .lores(List.of("&7" + transactionName() + " &c100 &7dollars."))
                            .build().make();
                }
            };


            customAmountButton = new Button() {
                @Override
                public void onClickedInMenu(final Player player, final Menu menu, final ClickType clickType) {
                    new ATMTransactionAmountPrompt(baseAccount, isDeposit).show(player);
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.SUNFLOWER)
                            .name(isDeposit ? "Deposit" : "Withdraw")
                            .lores(List.of("&7" + transactionName() + " a custom amount of money."))
                            .build().make();
                }
            };

            logButton = new Button() {
                @Override
                public void onClickedInMenu(final Player player, final Menu menu, final ClickType clickType) {
                    new TransactionMenu(menu, baseAccount).displayTo(player);
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.KNOWLEDGE_BOOK)
                            .name("&7Transaction logs")
                            .lores(List.of("&bClick to see the summary of this account transitions"))
                            .build().make();
                }
            };
        }

        @Override
        public ItemStack getItemAt(final int slot) {
            if (slot == 9 + 1) return oneDollarButton.getItem();
            if (slot == 9 + 2) return fiveDollarButton.getItem();
            if (slot == 9 + 3) return tenDollarButton.getItem();
            if (slot == 9 + 4) return twentyDollarButton.getItem();
            if (slot == 9 + 5) return oneHundredButton.getItem();

            if (slot == 9 + 7) return customAmountButton.getItem();
            if (slot == 9 + 8) return logButton.getItem();
            return null;
        }

        @Override
        protected String[] getInfo() {
            return new String[]{
                    "&cAccount Number: &7" + baseAccount.getCountNumber(),
                    "&bBalance: &7" + baseAccount.getBalance(),
                    "&bOwner: &7" + baseAccount.getOwner().getName()
            };
        }

        private void transaction(final ItemStack economyItem) {
            if (isDeposit) {
                ATM.depositAccountMoney(baseAccount, economyItem);
            } else
                ATM.withdrawAccountMoney(baseAccount, economyItem);

            restartMenu();
        }

        private String transactionName() {
            return isDeposit ? "Deposit" : "Withdraw";
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    // Bank Account menus
    // --------------------------------------------------------------------------------------------------------------

    public static class TransactionMenu extends MenuPagged<Transaction> {

        protected TransactionMenu(final Menu parent, final BaseAccount account) {
            super(parent, Transaction.getAccountTransaction(account));

            setTitle("&cTransaction Logs");
            addInfoButton();
            addReturnButton();
        }

        public TransactionMenu(final Menu parent, final EconomyPlayer player) {
            super(parent, player.getCountsTransaction());

            setTitle("&8Transaction Logs");
            addInfoButton();
            addReturnButton();
        }

        @Override
        protected ItemStack convertToItemStack(final Transaction type) {
            return ItemCreator.of(CompMaterial.PAPER)
                    .name("&7" + type.getType().getStringMessage() + " Transaction")
                    .lores(List.of(type.getPaid() != null ? "&aPaid&7: " + type.getPaid().getOwner().getName() : "",
                            "&cPayer&7: " + type.getPayer().getOwner().getName(),
                            "&eAmount&7: " + TransactionType.getAmountSign(type.getType()) + type.getAmount(),
                            "&8Time&7: " + TimeUtil.getFormattedDate(type.getTime() * 1000)))
                    .build().make();
        }

        @Override
        protected void onPageClick(final Player player, final Transaction item, final ClickType click) {

        }
    }


}
