package me.spiderdeluxe.mycteriaeconomy.commands;

import me.spiderdeluxe.mycteriaeconomy.models.machine.Machine;
import me.spiderdeluxe.mycteriaeconomy.models.shop.ShopListener;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import me.spiderdeluxe.mycteriaeconomy.util.SFXManager;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommand;

/**
 * @author wmorale01, SpiderDeluxe
 * This command is used to link a machine.
 */
public class LinkMachineCommand extends SimpleCommand {

	public LinkMachineCommand() {
		super("linkmachine");
		setDescription("Use this command for link a machine");
		setPermission("mycteriaeconomy.linkmachine");
	}

	@Override
	protected void onCommand() {
		checkConsole();
		final Player player = getPlayer();


		final Block targetedBlock = player.getTargetBlock(null, 5);
		final Machine machine = Machine.findMachine(targetedBlock.getLocation());

		if (machine == null || machine.getShop() == null) {
			Messager.sendErrorMessage(player, "&cYou must be targeting the machine you want "
					+ (ShopListener.isLinkers(player) ? "stop " : "") +
					"to link.");
			return;
		}

		if (!ShopListener.isLinkers(machine.getShop(), player)) {


			ShopListener.addLinkers(machine.getShop(), player);
			Messager.sendMessage(player, "&eRight Click the chests you want to link to this machine.");
			SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 0.6F, 1.4F);
			return;
		}
		ShopListener.removeLinkers(machine.getShop(), player);
		Messager.sendErrorMessage(player, "&cYou are not linking machines anymore.");
	}
}
