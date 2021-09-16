package me.wmorales01.mycteriaeconomy.commands.npc;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.NPCManager;
import me.wmorales01.mycteriaeconomy.models.ShopType;
import me.wmorales01.mycteriaeconomy.util.Messager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCCommandCreate extends NPCCommand {
    private final NPCManager npcManager;

    public NPCCommandCreate(MycteriaEconomy plugin) {
        this.npcManager = new NPCManager(plugin);

        setName("create");
        setInfoMessage("Creates a NPC shop.");
        setPermission("economyplugin.npcshop.create");
        setUsageMessage("/npcshop create <Name> <Skin> <ShopType>");
        setArgumentLength(4);
        setPlayerCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String npcName = args[1];
        String npcSkin = args[2];
        String shopTypeName = args[3];
        ShopType shopType = ShopType.fromName(shopTypeName);
        if (shopType == null) {
            Messager.sendErrorMessage(player, "&cUnknown Shop Type &l" + shopTypeName + "&c.");
            return;
        }
        npcManager.createNPC(player, npcName, npcSkin, shopType);
        Messager.sendSuccessMessage(player, "&aNPC shop successfully created!");
    }
}
