package me.wmorales01.mycteriaeconomy.commands;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.NPCManager;
import me.wmorales01.mycteriaeconomy.util.Messager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCCommandCreate extends NPCCommand {
    private MycteriaEconomy plugin;

    public NPCCommandCreate(MycteriaEconomy plugin) {
        this.plugin = plugin;

        setName("create");
        setHelpMessage("Creates a NPC shop.");
        setPermission("economyplugin.npcshop.create");
        setUsageMessage("/npcshop create <Skin>");
        setArgumentLength(2);
        setPlayerCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        NPCManager npcManager = new NPCManager(plugin);
        npcManager.createNPC(player, args[1]);
        Messager.sendSuccessMessage(player, "&aNPC shop has been successfully created!");
    }

}
