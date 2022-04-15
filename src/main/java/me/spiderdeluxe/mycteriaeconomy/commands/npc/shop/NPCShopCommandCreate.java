package me.spiderdeluxe.mycteriaeconomy.commands.npc.shop;

import lombok.SneakyThrows;
import me.spiderdeluxe.mycteriaeconomy.hook.ModelEngineHook;
import me.spiderdeluxe.mycteriaeconomy.models.npc.NPCShop;
import me.spiderdeluxe.mycteriaeconomy.models.shop.Shop;
import me.spiderdeluxe.mycteriaeconomy.util.EntityUtil;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * @author  SpiderDeluxe
 * This command is used to create a new npc shop
 */
public class NPCShopCommandCreate extends SimpleSubCommand {

	public NPCShopCommandCreate(final SimpleCommandGroup parent) {
		super(parent, "create");

		setName("create");
		 setDescription("Creates a NPC shop.");
		setPermission("mycteriaeconomy.npcshop.create");
		setUsage("<npcName> <shopName> <type> [modelName]");
		setMinArguments(3);
	}

	@SneakyThrows
	@Override
	public void onCommand() {
		checkConsole();

		final Player player = (Player) sender;
		final String npcName = args[0];
		final String shopName = args[1];

		checkBoolean(EntityUtil.isCorrectEntityType(args[2]), "You used an invalid entity type, try again using a working one!");

		final EntityType npcType = EntityType.valueOf(args[2].toUpperCase());

		final String modelName = args.length == 4 ? args[3] : null;

		final Location location = player.getTargetBlock(null, 5).getLocation().add(0, 1, 0);

		checkBoolean(!Shop.alreadyExist(shopName), "Sorry, already exist a shop with this name!");
		checkBoolean(npcType.isAlive(), "You used an invalid entity type, try again using a working one!");
		if(modelName != null)
			checkBoolean(ModelEngineHook.registeredModels.contains(modelName), "You used an invalid model type, try again using a working one!");

		final NPCShop npcShop = new NPCShop(null, npcName, shopName, npcType);
		npcShop.createNPC(location, modelName);

		Messager.sendSuccessMessage(player, "&aNPC shop successfully created!");
	}

	@Override
	protected List<String> tabComplete() {
		switch (args.length) {
			case 1:
			case 2:
				return completeLastWordPlayerNames();
			case 3:
				return completeLastWord(EntityType.values());
			case 4:
				return completeLastWord(ModelEngineHook.registeredModels);
		}
		return new ArrayList<>();
	}
}
