package me.spiderdeluxe.mycteriaeconomy.menu.bank;

import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.conversation.BankTransactionAmountPrompt;
import me.spiderdeluxe.mycteriaeconomy.menu.atm.MenuATMPagged;
import me.spiderdeluxe.mycteriaeconomy.models.account.AccountType;
import me.spiderdeluxe.mycteriaeconomy.models.account.BaseAccount;
import me.spiderdeluxe.mycteriaeconomy.models.bank.BaseBank;
import me.spiderdeluxe.mycteriaeconomy.util.CurrencyItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;

public class BankATMMenu extends MenuATMPagged<BaseAccount> {


    EconomyPlayer economyPlayer;
    BaseBank bank;

    public BankATMMenu(final EconomyPlayer economyPlayer, final BaseBank bank, final Menu parent) {
        super(parent, economyPlayer.getAccountInOrder());
        setSize(3 * 9);

        setTitle("&8Bank Vault");
        addInfoButton();

        this.economyPlayer = economyPlayer;
        this.bank = bank;
    }


    @Override
    protected ItemStack convertToItemStack(final BaseAccount count) {
        if (count.getType() == AccountType.PERSONAL)
            return ItemCreator.of(CompMaterial.PAPER).name("&aPersonal Account").lores(List.of("&7Account Number: &f" + count.getCountNumber(), "&7Balance: &f" + count.getBalance(), "&7Owner: &f" + count.getOwner().getName(),
                    "&7Left Click to &cDeposit &7or &cRight &7to Withdraw"
            )).build().make();

        else
            return ItemCreator.of(CompMaterial.PAPER).name("&aBusiness Account").lores(List.of("&7Account Number: &f" + count.getCountNumber(), "&7Balance: &f" + count.getBalance(), "&7Owner: &f" + count.getOwner().getName(),
                    "&7Left Click to &cDeposit &7or &cRight &7to Withdraw"
            )).build().make();
    }

    @Override
    protected void onPageClick(final Player player, final BaseAccount count, final ClickType clickType) {
        new WalletMenu(this, count, bank, clickType.isLeftClick()).displayTo(player);
    }


    @Override
    public Button formEmptyItemButton() {
        return new Button() {


            @Override
            public void onClickedInMenu(final Player player, final Menu menu, final ClickType click) {
            }

            @Override
            public ItemStack getItem() {

                return ItemCreator.of(CompMaterial.RED_STAINED_GLASS_PANE).name("&cEmpty Account").lore("&4This bank account has not yet been unlocked.").build().make();
            }
        };
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
        BaseBank bank;
        boolean isDeposit;

        Button oneDollarButton;
        Button fiveDollarButton;
        Button tenDollarButton;
        Button twentyDollarButton;

        Button customAmountButton;

        public WalletMenu(final Menu parent, final BaseAccount baseAccount, final BaseBank bank, final boolean isDeposit) {
            super(parent);
            setSize(9 * 3);
            setTitle(baseAccount.getType().getStringMessage() + " Account Menu");

            addReturnButton();
            addInfoButton();

            this.bank = bank;
            this.baseAccount = baseAccount;
            this.isDeposit = isDeposit;

            oneDollarButton = new Button() {
                @Override
                public void onClickedInMenu(final Player player, final Menu menu, final ClickType clickType) {
                    transaction(CurrencyItem.oneDollarBill());
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CurrencyItem.oneDollarBill()).lores(List.of("&7" + transactionName() + " &c1 &7dollar.")).build().make();
                }
            };

            fiveDollarButton = new Button() {
                @Override
                public void onClickedInMenu(final Player player, final Menu menu, final ClickType clickType) {
                    transaction(CurrencyItem.fiveDollarBill());

                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CurrencyItem.fiveDollarBill()).lores(List.of("&7" + transactionName() + " &c5 &7dollars.")).build().make();
                }
            };

            tenDollarButton = new Button() {
                @Override
                public void onClickedInMenu(final Player player, final Menu menu, final ClickType clickType) {
                    transaction(CurrencyItem.tenDollarBill());

                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CurrencyItem.oneDollarBill()).lores(List.of("&7" + transactionName() + " &c10 &7dollars.")).build().make();
                }
            };

            twentyDollarButton = new Button() {
                @Override
                public void onClickedInMenu(final Player player, final Menu menu, final ClickType clickType) {
                    transaction(CurrencyItem.twentyDollarBill());
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CurrencyItem.twentyDollarBill()).lores(List.of("&7" + transactionName() + " &c20 &7dollars.")).build().make();
                }
            };


            customAmountButton = new Button() {
                @Override
                public void onClickedInMenu(final Player player, final Menu menu, final ClickType clickType) {
                    new BankTransactionAmountPrompt(baseAccount, bank, isDeposit).show(player);
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.SUNFLOWER).name(isDeposit ? "Deposit" : "Withdraw").lores(List.of("&7" + transactionName() + " a custom amount of money.")).build().make();
                }
            };
        }

        @Override
        public ItemStack getItemAt(final int slot) {
            if (slot == 9 + 1) return oneDollarButton.getItem();
            if (slot == 9 + 2) return fiveDollarButton.getItem();
            if (slot == 9 + 3) return tenDollarButton.getItem();
            if (slot == 9 + 4) return twentyDollarButton.getItem();

            if (slot == 9 + 6) return customAmountButton.getItem();
            return null;
        }

        @Override
        protected String[] getInfo() {
            return new String[]{"&cAccount Number: &7" + baseAccount.getCountNumber(), "&bBalance: &7" + baseAccount.getBalance(), "&bOwner: &7" + baseAccount.getOwner().getName()};
        }

        private void transaction(final ItemStack economyItem) {
            if (isDeposit) {
                bank.deposit(baseAccount, economyItem);
            } else bank.withdraw(baseAccount, economyItem);

            restartMenu();
        }

        private String transactionName() {
            return isDeposit ? "Deposit" : "Withdraw";
        }
    }
}
