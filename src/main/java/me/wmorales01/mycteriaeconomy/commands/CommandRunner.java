package me.wmorales01.mycteriaeconomy.commands;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.AbstractMachine;
import me.wmorales01.mycteriaeconomy.util.Messager;
import me.wmorales01.mycteriaeconomy.util.SFXManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CommandRunner implements CommandExecutor {
    private final MycteriaEconomy plugin;

    public CommandRunner(MycteriaEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String ranCommand = cmd.getName();
        if (ranCommand.equalsIgnoreCase("economy") || ranCommand.equalsIgnoreCase("npcshop")) {
            return runSubcommand(sender, cmd, args);
        } else {
            return runCommand(sender, cmd, args);
        }
    }

    private boolean runSubcommand(CommandSender sender, Command cmd, String[] args) {
        String ranCommand = cmd.getName();
        Map<String, ? extends AbstractSubcommand> potentialSubcommands;
        if (ranCommand.equalsIgnoreCase("economy")) {
            potentialSubcommands = plugin.getEconomySubcommands();
        } else if (ranCommand.equalsIgnoreCase("npcshop")) {
            potentialSubcommands = plugin.getNpcSubcommands();
        } else {
            return false;
        }
        if (args.length == 0) {
            Messager.sendHelpMessage(sender);
            return true;
        }
        if (!potentialSubcommands.containsKey(args[0].toLowerCase())) {
            Messager.sendErrorMessage(sender, "&cUnknown command. Type &l/" + ranCommand.toLowerCase() +
                    " help &cto see the full command list.");
            return true;
        }
        AbstractSubcommand abstractSubcommand = potentialSubcommands.get(args[0].toLowerCase());
        if (!sender.hasPermission(abstractSubcommand.getPermission())) {
            Messager.sendNoPermissionMessage(sender);
            return true;
        }
        if (abstractSubcommand.isPlayerCommand() && !(sender instanceof Player)) {
            Messager.sendErrorMessage(sender, "&cNot available for consoles.");
            return true;
        }
        if (abstractSubcommand.isConsoleCommand() && sender instanceof Player) {
            Messager.sendErrorMessage(sender, "&cNot available for players.");
            return true;
        }
        if (args.length < abstractSubcommand.getArgumentLength()) {
            Messager.sendErrorMessage(sender, "&cUsage: &l" + abstractSubcommand.getUsageMessage());
            return true;
        }
        abstractSubcommand.execute(sender, args);
        return true;
    }

    private boolean runCommand(CommandSender sender, Command cmd, String[] args) {
        if (!(sender instanceof Player)) {
            Messager.sendErrorMessage(sender, "&cNot available for consoles.");
            return true;
        }
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("linkmachine")) {
            handleLinkStorageCommand(player);
        } else if (cmd.getName().equalsIgnoreCase("linknpc")) {
            /*if (!(sender instanceof Player)) {
                Messager.sendErrorMessage(sender, "&cNot available for consoles.");
                return true;
            }
            Player player = (Player) sender;
            if (!sender.hasPermission("economyplugin.linknpc")) {
                Messager.sendNoPermissionMessage(sender);
                return true;
            }
            if (!plugin.getNpcLinkers().containsKey(player)) {
                Block lookedBlock = player.getTargetBlock((Set<Material>) null, 5);
                if (lookedBlock.getType() != Material.CHEST) {
                    Messager.sendErrorMessage(player, "&cYou must be targeting a chest.");
                    return true;
                }
                plugin.getNpcLinkers().put(player, (Chest) lookedBlock.getState());
                Messager.sendMessage(sender, "&aYou must now click the NPC you want to link the chest to.");

            } else {
                plugin.getNpcLinkers().remove(player);
                Messager.sendSuccessMessage(player, "&cYou are not linking NPCs anymore.");
            }*/
        }
        return true;
    }

    private void handleLinkStorageCommand(Player player) {
        UUID playerUuid = player.getUniqueId();
        if (!plugin.getMachineLinkers().containsKey(playerUuid)) {
            Block targetedBlock = player.getTargetBlock((Set<Material>) null, 5);
            AbstractMachine machine = AbstractMachine.fromLocation(targetedBlock.getLocation());
            if (machine == null) {
                Messager.sendErrorMessage(player, "&cYou must be targeting the machine you want to link.");
                return;
            }
            plugin.getMachineLinkers().put(playerUuid, machine);
            Messager.sendMessage(player, "&eRight Click the chests you want to link to this machine.");
            SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 0.6F, 1.4F);
            return;
        }
        plugin.getMachineLinkers().remove(playerUuid);
        Messager.sendErrorMessage(player, "&cYou are not linking machines anymore.");
    }
}
