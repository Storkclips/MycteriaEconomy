package me.spiderdeluxe.mycteriaeconomy.cache;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.spiderdeluxe.mycteriaeconomy.models.npc.NPCBase;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class NPCStorage extends YamlConfig {

	@Getter
	private static final NPCStorage instance = new NPCStorage();

	@Getter
	private Set<NPCBase> activeNPCS = new HashSet<>();


	@Override
	protected void onLoadFinish() {
		activeNPCS.clear();

		if (isSet("NPC"))
			activeNPCS = getSet("NPC", NPCBase.class);
	}


	public void load() {
		loadConfiguration(null, "npc.yml");
	}

	//------------------------------------------------------------------
	// NPC manipulation
	//------------------------------------------------------------------
	public void addNPC(final NPCBase npcBase) {
		Valid.checkNotNull(npcBase, "This npc doesn't exists");


		activeNPCS.add(npcBase);
		save("NPC", activeNPCS);
	}

	public void removeNPC(final NPCBase npcBase) {

		activeNPCS.remove(npcBase);
		save("NPC", activeNPCS);
	}


}
