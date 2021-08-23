package me.wmorales01.mycteriaeconomy.listeners;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.ATM;
import me.wmorales01.mycteriaeconomy.util.Messager;

public class ATMBreak implements Listener {
	private MycteriaEconomy plugin;

	public ATMBreak(MycteriaEconomy instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onATMBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Location blockLocation = block.getLocation();
		ATM atm = ATM.getATM(blockLocation);
		if (atm == null)
			return;
		
		if (!player.hasPermission("economyplugin.createatm")) {
			Messager.sendNoPermissionMessage(player);
			return;
		}
		if (!plugin.getATMBreakers().contains(player)) {
			plugin.getATMBreakers().add(player);
			Messager.sendMessage(player, "&eAre you sure you want to break this ATM? If you are, break it again.");
			event.setCancelled(true);
			return;
		}
		plugin.getATMBreakers().remove(player);
		plugin.getATMs().remove(atm);
		
		blockLocation.getWorld().playSound(blockLocation, Sound.BLOCK_BEACON_DEACTIVATE, 1, 2);
		Messager.sendMessage(player, "&aYou removed this ATM.");
	}
}
