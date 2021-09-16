package me.wmorales01.mycteriaeconomy.commands.npc;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.NPCShopOperation;
import me.wmorales01.mycteriaeconomy.models.NPCShopOperator;
import me.wmorales01.mycteriaeconomy.util.Messager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NPCCommandConfigure extends NPCCommand {
    private final MycteriaEconomy plugin;

    public NPCCommandConfigure(MycteriaEconomy plugin) {
        this.plugin = plugin;

        setName("configure");
        setInfoMessage("Starts a configuration session for the next NPCs you click.");
        setPermission("mycteriaeconomy.npc.configure");
        setUsageMessage("/npcshop configure");
        setArgumentLength(1);
        setPlayerCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        UUID playerUuid = player.getUniqueId();
        NPCShopOperator npcShopOperator = plugin.getNpcShopOperators().get(playerUuid);
        if (npcShopOperator != null && npcShopOperator.getOperation() == NPCShopOperation.CONFIGURE) {
            // Stop configure session
            plugin.getNpcShopOperators().remove(playerUuid);
            Messager.sendErrorMessage(player, "&cYou are not configuring NPCShops anymore.");
            return;
        }
        // Start configure session
        npcShopOperator = new NPCShopOperator(NPCShopOperation.CONFIGURE);
        plugin.getNpcShopOperators().put(player.getUniqueId(), npcShopOperator);
        Messager.sendSuccessMessage(player, "&aYou are now configuring the NPCShops you right click.");
    }
}
