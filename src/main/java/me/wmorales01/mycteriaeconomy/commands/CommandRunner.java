package me.wmorales01.mycteriaeconomy.commands;

import me.wmorales01.mycteriaeconomy.models.Machine;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.util.Messager;

import java.util.Set;

public class CommandRunner implements CommandExecutor {
    private MycteriaEconomy plugin;

    public CommandRunner(MycteriaEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("npcshop")) {
            if (args.length == 0) {
                Messager.sendHelpMessage(sender);
                return true;
            }
            if (!plugin.getNpcSubcommands().containsKey(args[0].toLowerCase())) {
                Messager.sendErrorMessage(sender, "&cUnknown command. Type &l/ help &cto see the full command list.");
                return true;
            }
            NPCCommand subcommand = plugin.getNpcSubcommands().get(args[0].toLowerCase());
            if (!sender.hasPermission(subcommand.getPermission())) {
                Messager.sendNoPermissionMessage(sender);
                return true;
            }
            if (subcommand.isPlayerCommand() && !(sender instanceof Player)) {
                Messager.sendErrorMessage(sender, "&cNot available for consoles.");
                return true;
            }
            if (subcommand.isConsoleCommand() && sender instanceof Player) {
                Messager.sendErrorMessage(sender, "&cNot available for players.");
                return true;
            }
            if (args.length < subcommand.getArgumentLength()) {
                Messager.sendErrorMessage(sender, "&cUsage: &l" + subcommand.getUsageMessage());
                return true;
            }
            subcommand.execute(sender, args);

        } else if (cmd.getName().equalsIgnoreCase("linkmachine")) {
            if (!(sender instanceof Player)) {
                Messager.sendErrorMessage(sender, "&cNot available for consoles.");
                return true;
            }
            Player player = (Player) sender;
            if (!plugin.getMachineLinkers().containsKey(player)) {
                Block lookedBlock = player.getTargetBlock((Set<Material>) null, 5);
                Machine machine = Machine.getMachineAtLocation(lookedBlock.getLocation());
                if (machine == null) {
                    Messager.sendErrorMessage(player, "&cYou must be targeting the machine you want to link.");
                }
                plugin.getMachineLinkers().put((player), machine);
                Messager.sendMessage(sender, "&aYou must now click the chest you want to link the machine to.");

            } else {
                plugin.getMachineLinkers().remove(player);
                Messager.sendSuccessMessage(player, "&cYou are not linking machines anymore.");
            }

        } else if (cmd.getName().equalsIgnoreCase("linknpc")) {
            if (!(sender instanceof Player)) {
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
            }
        }
        return true;
    }
}
