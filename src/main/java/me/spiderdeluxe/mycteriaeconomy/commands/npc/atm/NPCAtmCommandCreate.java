package me.spiderdeluxe.mycteriaeconomy.commands.npc.atm;

import lombok.SneakyThrows;
import me.spiderdeluxe.mycteriaeconomy.hook.ModelEngineHook;
import me.spiderdeluxe.mycteriaeconomy.models.bank.BaseBank;
import me.spiderdeluxe.mycteriaeconomy.models.npc.NPCAtm;
import me.spiderdeluxe.mycteriaeconomy.models.npc.NPCBase;
import me.spiderdeluxe.mycteriaeconomy.util.EntityUtil;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author SpiderDeluxe
 * This command is used to create a new npc shop
 */
public class NPCAtmCommandCreate extends SimpleSubCommand {

	public NPCAtmCommandCreate(final SimpleCommandGroup parent) {
		super(parent, "create");

		setName("create");
		setDescription("Creates a NPC atm.");
		setPermission("mycteriaeconomy.atm.create");
		setUsage("<npcName> <type> [modelName]");
		setMinArguments(1);
	}

	@SneakyThrows
	@Override
	public void onCommand() {
		checkConsole();

		final Player player = (Player) sender;
		final String npcName = args[0];

		checkBoolean(EntityUtil.isCorrectEntityType(args[1]), "You used an invalid entity type, try again using a working one!");
		final EntityType npcType = EntityType.valueOf(args[1].toUpperCase(Locale.ROOT));

		final String modelName = args.length == 3 ? args[2] : null;

		checkBoolean(npcType.isAlive(), "You used an invalid entity type, try again using a working one!");

		if (modelName != null)
			checkBoolean(ModelEngineHook.registeredModels.contains(modelName), "You used an invalid model type, try again using a working one!");


		final Location location = player.getTargetBlock(null, 5).getLocation().add(0, 1, 0);

		checkBoolean(BaseBank.isWithin(location), "You are not within the bank area, so you cannot take this action.");

		final NPCAtm npcAtm = new NPCAtm(npcName, NPCBase.createNPCUUID(), npcType, null);
		npcAtm.createNPC(location, modelName);

		Messager.sendSuccessMessage(player, "&aNPC atm successfully created!");
	}

	@Override
	protected List<String> tabComplete() {
		switch (args.length) {
			case 1:
				return completeLastWordPlayerNames();
			case 2:
				return completeLastWord(EntityType.values());
			case 3:
				return completeLastWord(ModelEngineHook.registeredModels);
		}
		return new ArrayList<>();
	}
}
