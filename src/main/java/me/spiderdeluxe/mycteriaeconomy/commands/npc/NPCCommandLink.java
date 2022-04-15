package me.spiderdeluxe.mycteriaeconomy.commands.npc;

import me.spiderdeluxe.mycteriaeconomy.MycteriaEconomyPlugin;
import me.spiderdeluxe.mycteriaeconomy.models.npc.operator.NPCOperation;
import me.spiderdeluxe.mycteriaeconomy.models.npc.operator.NPCOperator;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import me.spiderdeluxe.mycteriaeconomy.util.SFXManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

/**
 * @author SpiderDeluxe
 * This command is used to start a link session to connect a chest and an NPCAtm.
 */
public class NPCCommandLink extends SimpleSubCommand {

	public NPCCommandLink(final SimpleCommandGroup parent) {
		super(parent, "link");
		setDescription("Starts a link session to connect a chest and an NPCAtm.");
		setPermission("mycteriaeconomy.npc.link");
	}

	@Override
	public void onCommand() {
		checkConsole();

		final MycteriaEconomyPlugin plugin = MycteriaEconomyPlugin.getInstance();

		final Player player = (Player) sender;
		final Block targetedBlock = player.getTargetBlock(null, 5);
		final Material targetedBlockType = targetedBlock.getType();
		final NPCOperator npcOperator = NPCOperator.getByPlayer(player);
		if (targetedBlockType == Material.CHEST || targetedBlockType == Material.TRAPPED_CHEST) {
			// Start link session
			final Chest chest = (Chest) targetedBlock.getState();
			new NPCOperator(player, NPCOperation.LINK, chest);
			Messager.sendMessage(player, "&eRight click the NPCs you want to link this chest with.");
			SFXManager.playPlayerSound(player, Sound.UI_BUTTON_CLICK, 0.6F, 1.4F);
			return;
		}
		if (npcOperator == null || npcOperator.getOperation() != NPCOperation.LINK) {
			Messager.sendErrorMessage(player, "&cYou must be looking at the chest you want to link.");
			return;
		}
		// Stop link session
		npcOperator.removeOperator();
		Messager.sendSuccessMessage(player, "&aYou are not linking chests anymore.");
	}
}
