package me.spiderdeluxe.mycteriaeconomy.menu.bank;

import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.models.bank.BankType;
import me.spiderdeluxe.mycteriaeconomy.models.bank.BaseBank;
import me.spiderdeluxe.mycteriaeconomy.models.bank.CommunityBank;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;

public class BankMenu extends MenuPagged<BaseBank> {
    EconomyPlayer economyPlayer;

    public BankMenu(final EconomyPlayer economyPlayer) {
        super(economyPlayer.getBanks());
        setTitle("Bank List");

        this.economyPlayer = economyPlayer;
    }

    @Override
    protected ItemStack convertToItemStack(final BaseBank bank) {
        final BankType type = bank.getType();

        if (type == BankType.STATE)
            return ItemCreator.of(CompMaterial.GOLD_BLOCK).name("&eState Treasury").lores(List.of("&fDescription: &7This is the most important bank in the state,", "&7as it contains the national treasury.")).build().make();
        if (type == BankType.LOCAL)
            return ItemCreator.of(CompMaterial.IRON_BLOCK).name("&cLocal Bank").lores(List.of("&fDescription: &7This bank is managed by the state,", "but is responsible for local business management")).build().make();
        if (type == BankType.COMMUNITY) {
            final CommunityBank communityBank = (CommunityBank) bank;
            final String owner = (communityBank.getOwner() != null ? communityBank.getOwner().getName() : "");

            return ItemCreator.of(CompMaterial.POLISHED_ANDESITE).name("&bCommunity Bank").lores(List.of("&fName: &7" + communityBank.getName(), "&fDescription: &7This bank is managed by " + owner)).build().make();
        }
        return null;
    }

    @Override
    protected void onPageClick(final Player player, final BaseBank bank, final ClickType click) {
        new BankEditMenu(bank, economyPlayer, this).displayTo(player);
    }


    public static class BankEditMenu extends Menu {
        BaseBank bank;
        EconomyPlayer player;

        Button balanceButton;
        Button branchButton;
        Button vaultButton;

        protected BankEditMenu(final BaseBank bank, final EconomyPlayer economyPlayer, final Menu parent) {
            super(parent);

            setTitle("&c" + bank.getType().getStringMessage() + " Bank");
            setSize(9);

            this.player = economyPlayer;
            this.bank = bank;

            balanceButton = new Button() {
                @Override
                public void onClickedInMenu(final Player player, final Menu menu, final ClickType click) {
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.GOLD_INGOT).name("&eBalance: &7" + bank.getBalance()).build().make();
                }
            };


            branchButton = new ButtonMenu(new BranchMenu(bank, this), CompMaterial.VILLAGER_SPAWN_EGG,
                    "&7Branch Manager",
                    "This section opens a menu to",
                    "be able to create/edit/remove a bank branch");

            vaultButton = new ButtonMenu(new BankATMMenu(economyPlayer, bank, this), CompMaterial.GOLD_BLOCK,
                    "&7Vault Manager",
                    "This section opens a menu to",
                    "to be able to deposit/withdraw money from your bank");

        }


        @Override
        public ItemStack getItemAt(final int slot) {
            if (slot == 0)
                return balanceButton.getItem();
            if (slot == 1)
                return branchButton.getItem();
            if (slot == 2)
                return vaultButton.getItem();

            return super.getItemAt(slot);
        }
    }

}