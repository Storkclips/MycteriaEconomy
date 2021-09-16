package me.wmorales01.mycteriaeconomy.commands.npc;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.NPCShopOperation;
import me.wmorales01.mycteriaeconomy.models.NPCShopOperator;
import me.wmorales01.mycteriaeconomy.util.Messager;
import me.wmorales01.mycteriaeconomy.util.SFXManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class NPCCommandLink extends NPCCommand {
    private final MycteriaEconomy plugin;

    public NPCCommandLink(MycteriaEconomy plugin) {
        this.plugin = plugin;

        setName("link");
        setInfoMessage("Starts a link session to connect a chest and an NPCShop.");
        setPermission("mycteriaeconomy.npc.link");
        setUsageMessage("/npcshop link");
        setArgumentLength(1);
        setPlayerCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        UUID playerUuid = player.getUniqueId();
        Block targetedBlock = player.getTargetBlock((Set<Material>) null, 5);
        Material targetedBlockType = targetedBlock.getType();
        NPCShopOperator npcShopOperator = plugin.getNpcShopOperators().get(playerUuid);
        if (targetedBlockType == Material.CHEST || targetedBlockType == Material.TRAPPED_CHEST) {
            // Start link session
            Chest chest = (Chest) targetedBlock.getState();
            plugin.getNpcShopOperators().put(playerUuid, new NPCShopOperator(NPCShopOperation.LINK, chest));
            Messager.sendMessage(player, "&eRight click the NPCs you want to link this chest with.");
            SFXManager.playPlayerSound(player, Sound.UI_BUTTON_CLICK, 0.6F, 1.4F);
            return;
        }
        if (npcShopOperator == null || npcShopOperator.getOperation() != NPCShopOperation.LINK) {
            Messager.sendErrorMessage(player, "&cYou must be looking at the chest you want to link.");
            return;
        }
        // Stop link session
        plugin.getNpcShopOperators().remove(playerUuid);
        Messager.sendSuccessMessage(player, "&aYou are not linking chests anymore.");
    }
}
