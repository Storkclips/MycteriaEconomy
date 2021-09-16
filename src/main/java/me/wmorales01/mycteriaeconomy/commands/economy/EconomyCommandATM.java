package me.wmorales01.mycteriaeconomy.commands.economy;

import me.wmorales01.mycteriaeconomy.models.ATM;
import me.wmorales01.mycteriaeconomy.util.InventoryUtil;
import me.wmorales01.mycteriaeconomy.util.Messager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EconomyCommandATM extends EconomyCommand {

    public EconomyCommandATM() {
        setName("atm");
        setInfoMessage("Gives you an ATM block.");
        setPermission("mycteriaeconomy.atm");
        setUsageMessage("/economy atm");
        setArgumentLength(1);
        setPlayerCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        ItemStack atm = ATM.getItemStack();
        InventoryUtil.giveItem(player, atm);
        Messager.sendSuccessMessage(player, "&aYou've received an ATM. Place it down to install it.");
    }
}
