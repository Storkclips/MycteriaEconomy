package me.spiderdeluxe.mycteriaeconomy;

import lombok.Getter;
import me.spiderdeluxe.mycteriaeconomy.commands.LinkMachineCommand;
import me.spiderdeluxe.mycteriaeconomy.commands.SetBalanceCommand;
import me.spiderdeluxe.mycteriaeconomy.commands.economy.EconomyCommandGroup;
import me.spiderdeluxe.mycteriaeconomy.commands.npc.atm.NPCAtmCommandGroup;
import me.spiderdeluxe.mycteriaeconomy.commands.npc.shop.NPCShopCommandGroup;
import me.spiderdeluxe.mycteriaeconomy.event.CurrencyGUIListener;
import me.spiderdeluxe.mycteriaeconomy.event.PlayerConnectionHandler;
import me.spiderdeluxe.mycteriaeconomy.event.WalletHandler;
import me.spiderdeluxe.mycteriaeconomy.files.EconomyPlayerManager;
import me.spiderdeluxe.mycteriaeconomy.files.WalletManager;
import me.spiderdeluxe.mycteriaeconomy.models.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.models.atm.ATMListener;
import me.spiderdeluxe.mycteriaeconomy.models.machine.Machine;
import me.spiderdeluxe.mycteriaeconomy.models.machine.MachineListener;
import me.spiderdeluxe.mycteriaeconomy.models.npc.NPCBase;
import me.spiderdeluxe.mycteriaeconomy.models.npc.NPCListener;
import me.spiderdeluxe.mycteriaeconomy.models.shop.Shop;
import me.spiderdeluxe.mycteriaeconomy.models.shop.ShopListener;
import me.spiderdeluxe.mycteriaeconomy.recipes.MachineRecipes;
import me.spiderdeluxe.mycteriaeconomy.recipes.WalletRecipe;
import me.spiderdeluxe.mycteriaeconomy.task.MachineVerifyTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MycteriaEconomyPlugin extends SimplePlugin {
	private final Map<Player, EconomyPlayer> economyPlayers = new HashMap<>();
	private WalletManager walletManager;
	private EconomyPlayerManager economyPlayerManager;

	public static MycteriaEconomyPlugin getInstance() {
		return MycteriaEconomyPlugin.getPlugin(MycteriaEconomyPlugin.class);
	}

	@Override
	public void onPluginStart() {
		Common.runLater(20 * 2, this::reloadCitizen);
		registerManagers();
		registerCommands();
		registerRecipes();
		registerListeners();

		Shop.loadAll();
		Common.runLater(20, NPCBase::loadAll);
		Common.runLater(20, Machine::loadAll);

		economyPlayerManager.loadOnlineEconomyPlayers();
	}

	@Override
	public void onPluginStop() {
		reloadCitizen();
		economyPlayerManager.saveOnlineEconomyPlayers();
		for (final Player player : Bukkit.getOnlinePlayers()) {
			player.closeInventory();
		}
	}

	private void registerManagers() {
		walletManager = new WalletManager(this);
		economyPlayerManager = new EconomyPlayerManager(this);
		Common.runTimer(20, new MachineVerifyTask());

	}

	private void registerCommands() {
		registerCommand(new SetBalanceCommand());
		registerCommand(new LinkMachineCommand());

		registerCommands("npcshop|shop", new NPCShopCommandGroup());
		registerCommands("npcatm|atm", new NPCAtmCommandGroup());
		registerCommands("economy|eco", new EconomyCommandGroup());

	}

	private void registerRecipes() {
		Bukkit.addRecipe(WalletRecipe.getWalletRecipe());
		Bukkit.addRecipe(MachineRecipes.getMachineRecipe());
	}

	private void registerListeners() {
		registerEvents(new ATMListener());
		registerEvents(new NPCListener());
		registerEvents(new ShopListener());
		registerEvents(new MachineListener());

		registerEvents(new PlayerConnectionHandler(this));
		registerEvents(new WalletHandler());
		registerEvents(new CurrencyGUIListener());
	}

	private void reloadCitizen() {
		Common.dispatchCommand(Bukkit.getConsoleSender(), "/citizens reload");
		Common.dispatchCommand(Bukkit.getConsoleSender(), "/citizens reload");
	}

}
