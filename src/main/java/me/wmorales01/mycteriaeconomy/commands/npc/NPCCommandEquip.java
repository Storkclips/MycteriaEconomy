package me.wmorales01.mycteriaeconomy.commands.npc;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.NPCShopOperation;
import me.wmorales01.mycteriaeconomy.models.NPCShopOperator;
import me.wmorales01.mycteriaeconomy.util.Messager;
import me.wmorales01.mycteriaeconomy.util.SFXManager;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class NPCCommandEquip extends NPCCommand {
    private final MycteriaEconomy plugin;

    public NPCCommandEquip(MycteriaEconomy plugin) {
        this.plugin = plugin;

        setName("equip");
        setInfoMessage("Starts an NPCShop equip session.");
        setPermission("mycteriaeconomy.npc.equip");
        setUsageMessage("/npcshop equip <EquipmentSlot>");
        setArgumentLength(2);
        setPlayerCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        UUID playerUuid = player.getUniqueId();
        NPCShopOperator npcShopOperator = plugin.getNpcShopOperators().get(playerUuid);
        String equipmentSlotName = args[1].toUpperCase();
        EquipmentSlot equipmentSlot;
        try {
            equipmentSlot = EquipmentSlot.valueOf(equipmentSlotName);
        } catch (Exception e) {
            Messager.sendErrorMessage(player, "&cUnknown equipment slot &l" + equipmentSlotName + "&c.");
            return;
        }
        if (npcShopOperator != null && npcShopOperator.getOperation() == NPCShopOperation.EQUIPMENT
                && npcShopOperator.getConfiguringSlot() == equipmentSlot) {
            // Stop equip session
            plugin.getNpcShopOperators().remove(playerUuid);
            Messager.sendSuccessMessage(player, "&aYou are not equipping items to NPCs anymore.");
            return;
        }
        npcShopOperator = new NPCShopOperator(NPCShopOperation.EQUIPMENT, equipmentSlot);
        plugin.getNpcShopOperators().put(playerUuid, npcShopOperator);
        Messager.sendMessage(player, "&aRight click the NPCs you want to modify their &l" + equipmentSlotName +
                " &awith the desired item to equip in your main hand.");
        SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.6F, 1.4F);
    }
}
