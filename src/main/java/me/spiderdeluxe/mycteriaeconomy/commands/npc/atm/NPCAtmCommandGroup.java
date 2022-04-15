package me.spiderdeluxe.mycteriaeconomy.commands.npc.atm;

import org.mineacademy.fo.command.SimpleCommandGroup;

/**
 * @author SpiderDeluxe
 * This class handles all commands starting with /atm
 */
public class NPCAtmCommandGroup extends SimpleCommandGroup {

	@Override
	protected void registerSubcommands() {
		registerSubcommand(new NPCAtmCommandCreate(this));
		registerSubcommand(new NPCAtmCommandDelete(this));
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
