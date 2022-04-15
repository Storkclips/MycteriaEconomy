package me.spiderdeluxe.mycteriaeconomy.menu.bank;

import me.spiderdeluxe.mycteriaeconomy.conversation.BranchCreatePrompt;
import me.spiderdeluxe.mycteriaeconomy.models.bank.BaseBank;
import me.spiderdeluxe.mycteriaeconomy.models.bank.Branch;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonConversation;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;

public class BranchMenu extends Menu {
    BaseBank bank;


    Button createBranch;
    Button editBranch;
    Button deleteBranch;

    protected BranchMenu(final BaseBank bank, final Menu parent) {
        super(parent);
        this.bank = bank;

        setTitle("Branch Menu");

        setSize(9);
        createBranch = new ButtonConversation(new BranchCreatePrompt(bank), CompMaterial.EMERALD,
                "&7Create Branch",
                "This section is used to",
                "create a new branch of your bank");

        editBranch = new ButtonMenu(new BranchEditMenu(bank, this), CompMaterial.ANVIL,
                "&7Edit Branch", "Here you can enable the branch editing mode");

        deleteBranch = new ButtonMenu(new BranchRemoveMenu(bank, this), CompMaterial.RED_BANNER,
                "&7Remove Branch", "Here you can remove on of the branch");


    }

    @Override
    public ItemStack getItemAt(final int slot) {
        if (slot == 1)
            return createBranch.getItem();
        if (slot == 2)
            return editBranch.getItem();
        if (slot == 3)
            return deleteBranch.getItem();
        return null;
    }

    public static class BranchEditMenu extends MenuPagged<Branch> {


        public BranchEditMenu(final BaseBank bank, final Menu parent) {
            super(parent, bank.getBranches());

            setTitle("Branch Edit");

        }


        @Override
        protected ItemStack convertToItemStack(final Branch item) {
            return branchItem(item, item.getEditingPlayer() == getViewer(), "Click to edit this branch");
        }

        @Override
        protected void onPageClick(final Player player, final Branch item, final ClickType click) {
            if (Branch.isEditingPlayer(player)) {
                Messenger.error(player, "You are already editing a branch, you must disable it before activating another one");
                return;
            }
            item.setEditingPlayer(player);
            restartMenu();
        }
    }

    public class BranchRemoveMenu extends MenuPagged<Branch> {


        protected BranchRemoveMenu(final BaseBank bank, final Menu parent) {
            super(parent, bank.getBranches());

            setTitle("Branch Delete");

        }

        @Override
        protected ItemStack convertToItemStack(final Branch item) {
            return branchItem(item, false, "Click to remove this branch");
        }

        @Override
        protected void onPageClick(final Player player, final Branch item, final ClickType click) {
            bank.deleteBranch(item);
            restartMenu("&bBranch removed!");
        }
    }


    public static ItemStack branchItem(final Branch branch, final Boolean glow, final String extraInfo) {
        return ItemCreator.of(CompMaterial.IRON_BARS)
                .name(branch.getName())
                .lores(List.of(
                        "&7Name: &f" + branch.getName(),
                        branch.getRegion() != null && branch.getRegion().isWhole() ? "&7Region: &f" + branch.getRegionString() : "&7Region: &fnone",
                        extraInfo != null ? extraInfo : ""))
                .glow(glow)
                .build().make();
    }
}