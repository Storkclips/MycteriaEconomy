package me.spiderdeluxe.mycteriaeconomy;

import lombok.Getter;
import me.spiderdeluxe.mycteriaeconomy.cache.DataStorage;
import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.cache.NPCStorage;
import me.spiderdeluxe.mycteriaeconomy.commands.CreditCommand;
import me.spiderdeluxe.mycteriaeconomy.commands.LinkMachineCommand;
import me.spiderdeluxe.mycteriaeconomy.commands.bank.BankCommandGroup;
import me.spiderdeluxe.mycteriaeconomy.commands.business.BusinessCommandGroup;
import me.spiderdeluxe.mycteriaeconomy.commands.economy.EconomyCommandGroup;
import me.spiderdeluxe.mycteriaeconomy.commands.loan.LoanCommandGroup;
import me.spiderdeluxe.mycteriaeconomy.commands.npc.atm.NPCAtmCommandGroup;
import me.spiderdeluxe.mycteriaeconomy.commands.npc.shop.NPCShopCommandGroup;
import me.spiderdeluxe.mycteriaeconomy.commands.npc.tellers.NPCTellersCommandGroup;
import me.spiderdeluxe.mycteriaeconomy.commands.work.WorkCommandGroup;
import me.spiderdeluxe.mycteriaeconomy.event.CurrencyGUIListener;
import me.spiderdeluxe.mycteriaeconomy.event.WalletHandler;
import me.spiderdeluxe.mycteriaeconomy.files.WalletManager;
import me.spiderdeluxe.mycteriaeconomy.models.bank.Loan;
import me.spiderdeluxe.mycteriaeconomy.models.bank.transaction.Transaction;
import me.spiderdeluxe.mycteriaeconomy.models.machine.MachineListener;
import me.spiderdeluxe.mycteriaeconomy.models.npc.NPCListener;
import me.spiderdeluxe.mycteriaeconomy.models.shop.Shop;
import me.spiderdeluxe.mycteriaeconomy.models.shop.ShopListener;
import me.spiderdeluxe.mycteriaeconomy.models.work.BaseWork;
import me.spiderdeluxe.mycteriaeconomy.recipes.MachineRecipes;
import me.spiderdeluxe.mycteriaeconomy.recipes.WalletRecipe;
import me.spiderdeluxe.mycteriaeconomy.task.MachineVerifyTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;

@Getter
public class MycteriaEconomyPlugin extends SimplePlugin {
	@Getter
	private static MycteriaEconomyPlugin instance;

	private WalletManager walletManager;

	@Override
	public void onPluginStart() {
		instance = this;
		registerManagers();
		registerCommands();
		registerRecipes();
		registerListeners();

		Shop.loadAll();
		Common.runLater(20, this::reloadCitizen);
		Common.runLater(20 * 2, () -> NPCStorage.getInstance().load());
		Common.runLater(20 * 2, () -> DataStorage.getInstance().load());

		//Take commissions from players
		Common.runTimer((20 * 60) * 2, Loan::checkPlayerInstallment);
		Common.runTimer((20 * 60) * 60, Transaction::clearPlayerTransaction);
		Common.runTimer((20 * 60) * 2, BaseWork::checkSalaryPayment);
	}

	@Override
	public void onPluginStop() {

		for (final Player player : Bukkit.getOnlinePlayers()) {
			player.closeInventory();

			final EconomyPlayer economyPlayer = EconomyPlayer.from(player);
			economyPlayer.saveAccounts();
		}

		DataStorage.getInstance().saveData();
	}

	private void registerManagers() {
		walletManager = new WalletManager(this);
		Common.runTimer(20, new MachineVerifyTask());

	}

	private void registerCommands() {
		registerCommand(new LinkMachineCommand());
		registerCommand(new CreditCommand());

		registerCommands("loan|loans", new LoanCommandGroup());
		registerCommands("npcshop|shop", new NPCShopCommandGroup());
		registerCommands("npcatm|atm", new NPCAtmCommandGroup());
		registerCommands("economy|eco", new EconomyCommandGroup());
		registerCommands("bankaccount|bank", new BankCommandGroup());
		registerCommands("business", new BusinessCommandGroup());
		registerCommands("work|job", new WorkCommandGroup());
		registerCommands("teller", new NPCTellersCommandGroup());
	}

	private void registerRecipes() {
		Bukkit.addRecipe(WalletRecipe.getWalletRecipe());
		Bukkit.addRecipe(MachineRecipes.getMachineRecipe());
	}

	private void registerListeners() {
		registerEvents(new NPCListener());
		registerEvents(new ShopListener());
		registerEvents(new MachineListener());

		registerEvents(new WalletHandler());
		registerEvents(new CurrencyGUIListener());
	}

	private void reloadCitizen() {
		Common.dispatchCommand(Bukkit.getConsoleSender(), "/citizens reload");
		Common.dispatchCommand(Bukkit.getConsoleSender(), "/citizens reload");
	}

}
