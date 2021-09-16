package me.wmorales01.mycteriaeconomy.commands.npc;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.NPCShopOperation;
import me.wmorales01.mycteriaeconomy.models.NPCShopOperator;
import me.wmorales01.mycteriaeconomy.util.Messager;
import me.wmorales01.mycteriaeconomy.util.SFXManager;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NPCCommandDelete extends NPCCommand {
    private final MycteriaEconomy plugin;

    public NPCCommandDelete(MycteriaEconomy plugin) {
        this.plugin = plugin;

        setName("delete");
        setInfoMessage("Starts a delete session for NPC shops.");
        setPermission("mycteriaeconomy.npc.delete");
        setUsageMessage("/npcshop delete");
        setArgumentLength(1);
        setPlayerCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        UUID playerUuid = player.getUniqueId();
        NPCShopOperator npcShopOperator = plugin.getNpcShopOperators().get(playerUuid);
        if (npcShopOperator == null || npcShopOperator.getOperation() != NPCShopOperation.DELETE) {
            // Start delete session
            npcShopOperator = new NPCShopOperator(NPCShopOperation.DELETE);
            plugin.getNpcShopOperators().put(playerUuid, npcShopOperator);
            Messager.sendMessage(player, "&aYou are now deleting all the NPCs you right click.");
            SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.6F, 1.4F);
            return;
        }
        // Stop delete session
        plugin.getNpcShopOperators().remove(playerUuid);
        Messager.sendSuccessMessage(player, "&aYou are not deleting NPCs anymore.");
    }
}
