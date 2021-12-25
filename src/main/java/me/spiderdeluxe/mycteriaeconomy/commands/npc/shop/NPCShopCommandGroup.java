package me.spiderdeluxe.mycteriaeconomy.commands.npc.shop;

import org.mineacademy.fo.command.SimpleCommandGroup;

/**
 * @author SpiderDeluxe
 * This class handles all commands starting with /npc
 */
public class NPCShopCommandGroup extends SimpleCommandGroup {

	@Override
	protected void registerSubcommands() {
		//Manipulation shop commands
		registerSubcommand(new NPCShopCommandCreate(this));
		registerSubcommand(new NPCShopCommandDelete(this));

		//Managing shop commands
		registerSubcommand(new NPCShopCommandLink(this));
	}

	@Override
	protected String getCredits() {
	return "Plugin that manages the economy of server.";
	}

	@Override
	protected String getHeaderPrefix() {
		return "{#fe7916}&l";
	}

}
