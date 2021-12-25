package me.spiderdeluxe.mycteriaeconomy.commands.economy;

import me.spiderdeluxe.mycteriaeconomy.models.Wallet;
import me.spiderdeluxe.mycteriaeconomy.util.InventoryUtil;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

/**
 * @author wmorale01, SpiderDeluxe
 * This command is used to give you a new Wallet.
 */
public class EconomyCommandWallet extends SimpleSubCommand {

    public EconomyCommandWallet(final SimpleCommandGroup parent) {
        super(parent, "wallet");

        setDescription("Gives you a new Wallet.");
        setPermission("mycteriaeconomy.wallet");
    }

    @Override
    public void onCommand() {
        checkConsole();

        final Player player = (Player) sender;
        final Wallet wallet = new Wallet();
        InventoryUtil.giveItem(player, wallet.getItemStack());
        Messager.sendSuccessMessage(player, "&aYou received a brand new Wallet!");
    }
}
