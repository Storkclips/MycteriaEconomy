package me.wmorales01.mycteriaeconomy.commands.economy;

import me.wmorales01.mycteriaeconomy.models.Wallet;
import me.wmorales01.mycteriaeconomy.util.InventoryUtil;
import me.wmorales01.mycteriaeconomy.util.Messager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyCommandWallet extends EconomyCommand {

    public EconomyCommandWallet() {
        setName("wallet");
        setInfoMessage("Gives you a new Wallet.");
        setPermission("mycteriaeconomy.wallet");
        setUsageMessage("/economy wallet");
        setArgumentLength(1);
        setPlayerCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Wallet wallet = new Wallet();
        InventoryUtil.giveItem(player, wallet.getItemStack());
        Messager.sendSuccessMessage(player, "&aYou received a brand new Wallet!");
    }
}
