package me.spiderdeluxe.mycteriaeconomy.commands.npc.atm;

import me.spiderdeluxe.mycteriaeconomy.commands.npc.shop.NPCShopCommandLink;
import org.mineacademy.fo.command.SimpleCommandGroup;

/**
 * @author SpiderDeluxe
 * This class handles all commands starting with /atm
 */
public class NPCAtmCommandGroup extends SimpleCommandGroup {

	@Override
	protected void registerSubcommands() {
		//Manipulation shop commands
		registerSubcommand(new NPCAtmCommandCreate(this));
		registerSubcommand(new NPCAtmCommandDelete(this));

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
