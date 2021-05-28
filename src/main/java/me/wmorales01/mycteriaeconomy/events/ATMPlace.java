package me.wmorales01.mycteriaeconomy.events;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.ATM;
import me.wmorales01.mycteriaeconomy.util.Messager;

public class ATMPlace implements Listener {
	private MycteriaEconomy plugin;

	public ATMPlace(MycteriaEconomy instance) {
		plugin = instance;
	}

	@EventHandler
	public void onATMPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		ItemStack handItem = player.getInventory().getItemInMainHand();
		if (handItem == null || !handItem.isSimilar(ATM.getATMItem())) {
			handItem = player.getInventory().getItemInOffHand();
			if (handItem == null || !handItem.isSimilar(ATM.getATMItem()))
				return;
		}
		if (!plugin.getATMPlacers().contains(player))
			return;
		if (!player.hasPermission("economyplugin.createatm")) {
			Messager.sendNoPermissionMessage(player);
			return;
		}
		
		Location location = event.getBlock().getLocation();
		ATM atm = new ATM(location);
		plugin.getATMs().add(atm);
		location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 5);
	}
}
