package me.wmorales01.mycteriaeconomy.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.ATM;
import me.wmorales01.mycteriaeconomy.models.EconomyPlayer;

public class ATMOpen implements Listener {
	private MycteriaEconomy plugin;

	public ATMOpen(MycteriaEconomy instance) {
		plugin = instance;
	}

	@EventHandler
	public void onATMOpen(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		Block rightClickedBlock = event.getClickedBlock();
		if (rightClickedBlock.getType() != Material.DISPENSER)
			return;
		Location blockLocation = rightClickedBlock.getLocation();
		ATM atm = ATM.getATM(blockLocation);
		if (atm == null)
			return;
		
		event.setCancelled(true);
		
		Player player = event.getPlayer();
		EconomyPlayer ecoPlayer = EconomyPlayer.fromPlayer(player);
		if (ecoPlayer == null) {
			ecoPlayer = new EconomyPlayer(player);
			plugin.getEconomyPlayers().add(ecoPlayer);
		}
		
		event.getPlayer().openInventory(ATM.getWithdrawATMGUI(ecoPlayer.getBankBalance()));
	}
}
