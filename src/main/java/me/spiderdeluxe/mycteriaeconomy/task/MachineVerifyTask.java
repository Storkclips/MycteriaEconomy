package me.spiderdeluxe.mycteriaeconomy.task;

import me.spiderdeluxe.mycteriaeconomy.models.machine.Machine;
import me.spiderdeluxe.mycteriaeconomy.models.shop.ShopListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class MachineVerifyTask extends BukkitRunnable {
	@Override
	public void run() {

		final Collection<Machine> machines = Machine.getMachines();
		if(!machines.isEmpty())
		for (final Machine machine : machines) {

			if (!Machine.isMachineBlock(machine.getLocation().getBlock())) {
				ShopListener.removeLinkers(machine.getShop(), machine.getPlayer());
				ShopListener.removeCustomizer(machine.getPlayer().getPlayer());

				machine.deleteMachine();
				return;
			}
		}
	}
}