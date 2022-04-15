package me.spiderdeluxe.mycteriaeconomy.menu.tellers;

import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.menu.atm.ATMMenu;
import me.spiderdeluxe.mycteriaeconomy.menu.atm.MenuATMPagged;
import me.spiderdeluxe.mycteriaeconomy.models.account.AccountType;
import me.spiderdeluxe.mycteriaeconomy.models.account.BaseAccount;
import me.spiderdeluxe.mycteriaeconomy.models.account.PersonalAccount;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;

import java.util.List;
import java.util.Objects;

public class TellersMenu extends MenuATMPagged<BaseAccount> {


    EconomyPlayer economyPlayer;

    Button logButton;
    Button redeemButton;

    public TellersMenu(final EconomyPlayer economyPlayer) {
        super(economyPlayer.getAccountInOrder());
        setSize(3 * 9);

        setTitle("&a&lBank's Panel");
        addInfoButton();

        this.economyPlayer = economyPlayer;


        logButton = new Button() {
            @Override
            public void onClickedInMenu(final Player player, final Menu menu, final ClickType clickType) {
                new ATMMenu.TransactionMenu(menu, economyPlayer).displayTo(player);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.KNOWLEDGE_BOOK)
                        .name("&7Transaction logs")
                        .lores(List.of("&bClick to see the summary of your transitions"))
                        .build().make();
            }
        };

        redeemButton = new Button() {
            @Override
            public void onClickedInMenu(final Player player, final Menu menu, final ClickType click) {
                for (final ItemStack item : player.getInventory().getContents())
                    if (item != null && CompMetadata.hasMetadata(item, "EMPLOYER_NAME")
                            && Objects.equals(CompMetadata.getMetadata(item, "EMPLOYER_NAME"), player.getName())) {
                        item.setType(Material.AIR);

                        final PersonalAccount account = economyPlayer.getPersonalAccount();
                        account.withdraw(Integer.parseInt(CompMetadata.getMetadata(item, "EMPLOYER_SALARY")));
                    }
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.PAPER)
                        .name("&7Redeem Receipts")
                        .lores(List.of("&bClick to redeem receipts in your inventory"))
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
                            "&7Owner: &f" + count.getOwner().getName(),
                            "&7Left Click to &cDeposit &7or &cRight &7to Withdraw"
                    ))
                    .build().make();
        else
            return ItemCreator.of(CompMaterial.PAPER)
                    .name("&aBusiness Account")
                    .lores(List.of("&7Account Number: &f" + count.getCountNumber(),
                            "&7Balance: &f" + count.getBalance(),
                            "&7Owner: &f" + count.getOwner().getName(),
                            "&7Left Click to &cDeposit &7or &cRight &7to Withdraw"))
                    .build().make();
    }

    @Override
    protected void onPageClick(final Player player, final BaseAccount count, final ClickType clickType) {
        new ATMMenu.WalletMenu(this, count, clickType.isLeftClick()).displayTo(player);
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
        if (slot == 17)
            return redeemButton.getItem();

        return super.getItemAt(slot);
    }

    @Override
    protected String[] getInfo() {
        return super.getInfo();
    }


}
