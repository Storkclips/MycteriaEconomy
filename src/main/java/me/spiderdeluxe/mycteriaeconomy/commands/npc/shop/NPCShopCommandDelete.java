package me.spiderdeluxe.mycteriaeconomy.commands.npc.shop;

import me.spiderdeluxe.mycteriaeconomy.models.npc.operator.NPCOperator;
import me.spiderdeluxe.mycteriaeconomy.models.npc.operator.NPCShopOperation;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import me.spiderdeluxe.mycteriaeconomy.util.SFXManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

/**
 * @author wmorale01, SpiderDeluxe
 * This command is used to delete a npc shop
 */
public class NPCShopCommandDelete extends SimpleSubCommand {

	public NPCShopCommandDelete(final SimpleCommandGroup parent) {
		super(parent, "delete");

		setDescription("Starts a delete session for NPC shops.");
		setPermission("mycteriaeconomy.npc.delete");
	}

	@Override
	public void onCommand() {
		checkConsole();

		final Player player = (Player) sender;
		final NPCOperator npcOperator = NPCOperator.getByPlayer(player);
		if (npcOperator == null || npcOperator.getOperation() != NPCShopOperation.DELETE) {
            if (npcOperator != null &&
                    npcOperator.getOperation() != NPCShopOperation.DELETE)
				npcOperator.removeOperator();
			// Start delete session
			new NPCOperator(player, NPCShopOperation.DELETE);
			Messager.sendMessage(player, "&aYou are now deleting all the NPCs you right click.");
			SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.6F, 1.4F);
			return;
		}
		// Stop delete session
        npcOperator.removeOperator();
		Messager.sendSuccessMessage(player, "&aYou are not deleting NPCs anymore.");
	}
}
