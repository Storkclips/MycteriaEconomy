package me.spiderdeluxe.mycteriaeconomy.commands.npc.tellers;

import org.mineacademy.fo.command.SimpleCommandGroup;

/**
 * @author SpiderDeluxe
 * This class handles all commands starting with /atm
 */
public class NPCTellersCommandGroup extends SimpleCommandGroup {

	@Override
	protected void registerSubcommands() {
		registerSubcommand(new NPCTellersCommandCreate(this));
		registerSubcommand(new NPCTellersCommandDelete(this));
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
