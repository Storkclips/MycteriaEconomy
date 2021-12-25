package me.spiderdeluxe.mycteriaeconomy.cache;

import lombok.Getter;
import lombok.Setter;
import me.spiderdeluxe.mycteriaeconomy.models.machine.Machine;
import me.spiderdeluxe.mycteriaeconomy.models.npc.NPCBase;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListStorage extends YamlConfig {


	@Getter
	private static final ListStorage instance = new ListStorage();

	@Setter
	@Getter
	private List<String> atmList = new ArrayList<>();

	@Setter
	@Getter
	private List<String> npcList = new ArrayList<>();

	@Setter
	@Getter
	private Set<String> machinesList = new HashSet<>();


	@Setter
	@Getter
	private double nationalFounds;

	/**
	 * This file contains the list of the names of all the atm in the server.
	 */
	public ListStorage() {

		loadConfiguration(null, "data.db");
	}


	@Override
	protected void onLoadFinish() {
		atmList.clear();
		npcList.clear();
		machinesList.clear();

		if (isSet("NPCs List")) {
			npcList = getStringList("NPCs List");
		}

		if (isSet("Machines List")) {
			machinesList = getSet("Machines List", String.class);
		}

		if (isSet("National Founds")) {
			nationalFounds = getDouble("National Founds");
		}
	}

	// --------------------------------------------------------------------------------------------------------------
	// Npc List manipulation
	// --------------------------------------------------------------------------------------------------------------

	public void addNPC(final NPCBase npcBase) {

		npcList.add(npcBase.getUuid().toString());
		save("NPCs List", npcList);
	}

	public void removeNPC(final NPCBase npcBase) {
		Valid.checkNotNull(npcBase, "This npc doesn't exists");

		npcList.remove(npcBase.getUuid().toString());
		save("NPCs List", npcList);

	}

	// --------------------------------------------------------------------------------------------------------------
	// Machines List manipulation
	// --------------------------------------------------------------------------------------------------------------

	public void addMachine(final Player player) {

		machinesList.add(player.getUniqueId().toString());
		save("Machines List", machinesList);
	}

	public void removeMachine(final Machine machine) {
		Valid.checkNotNull(machine, "This machine doesn't exists");

		machinesList.remove(machine.getPlayer().getUniqueId().toString());
		save("Machines List", machinesList);

	}

	// --------------------------------------------------------------------------------------------------------------
	// National Founds manipulation
	// --------------------------------------------------------------------------------------------------------------

	public void increaseFounds(final double amount) {

		nationalFounds += amount;
		save("National Founds", nationalFounds);

	}

	public void decreaseFounds(final double amount) {

		nationalFounds -= amount;
		save("National Founds", nationalFounds);
	}

}
