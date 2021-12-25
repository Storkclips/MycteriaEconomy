package me.spiderdeluxe.mycteriaeconomy.commands.economy;

import org.mineacademy.fo.command.SimpleCommandGroup;

/**
 * @author SpiderDeluxe
 * This class handles all commands starting with /economy
 */
public class EconomyCommandGroup extends SimpleCommandGroup {

	@Override
	protected void registerSubcommands() {
		registerSubcommand(new EconomyCommandCash(this));
		registerSubcommand(new EconomyCommandBalance(this));
		registerSubcommand(new EconomyCommandReload(this));
		registerSubcommand(new EconomyCommandWallet(this));
		registerSubcommand(new EconomyCommandCurrency(this));
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
