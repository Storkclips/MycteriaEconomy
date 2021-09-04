package me.wmorales01.mycteriaeconomy.commands;

import me.wmorales01.mycteriaeconomy.models.NPCTool;
import me.wmorales01.mycteriaeconomy.util.Messager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class NPCCommandTool extends NPCCommand {

    public NPCCommandTool() {
        setName("tool");
        setInfoMessage("Gives you the NPC tool.");
        setPermission("economyplugin.npc.tool");
        setUsageMessage("/npcshop tool");
        setArgumentLength(1);
        setPlayerCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();
        ItemStack tool = NPCTool.getItemStack();
        if (inventory.firstEmpty() == -1)
            player.getWorld().dropItemNaturally(player.getLocation(), tool);
        else
            inventory.addItem(tool);

        Messager.sendSuccessMessage(player, "&aYou received the NPC tool!");

    }

}
