package me.wmorales01.mycteriaeconomy;

import me.wmorales01.mycteriaeconomy.commands.CommandCompleter;
import me.wmorales01.mycteriaeconomy.commands.CommandRunner;
import me.wmorales01.mycteriaeconomy.commands.economy.*;
import me.wmorales01.mycteriaeconomy.commands.npc.*;
import me.wmorales01.mycteriaeconomy.files.*;
import me.wmorales01.mycteriaeconomy.listeners.*;
import me.wmorales01.mycteriaeconomy.models.*;
import me.wmorales01.mycteriaeconomy.recipes.MachineRecipes;
import me.wmorales01.mycteriaeconomy.recipes.WalletRecipe;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class MycteriaEconomy extends JavaPlugin {
    private final Map<String, EconomyCommand> economySubcommands = new LinkedHashMap<>();
    private final Map<String, NPCCommand> npcSubcommands = new LinkedHashMap<>();
    private final Map<Player, EconomyPlayer> economyPlayers = new HashMap<>();
    private final Map<Location, ATM> atms = new HashMap<>();
    private final Map<Location, AbstractMachine> machines = new HashMap<>(); // Stores the Trading and Vending Machines of the server
    private final Map<UUID, AbstractMachine> machineLinkers = new HashMap<>(); // Stores the players that are linking chests to machines
    private final Map<EntityPlayer, AbstractNPCShop> npcShops = new HashMap<>(); // Stores all the NPC Shops of the server
    private final Map<UUID, NPCShopOperator> npcShopOperators = new HashMap<>(); // Stores NPCShopOperators of the server
    private WalletManager walletManager;
    private ATMManager atmManager;
    private MachineManager machineManager;
    private EconomyPlayerManager economyPlayerManager;
    private NPCDataManager npcDataManager;

    public static MycteriaEconomy getInstance() {
        return MycteriaEconomy.getPlugin(MycteriaEconomy.class);
    }

    @Override
    public void onEnable() {
        createConfig();
        registerManagers();
        registerCommands();
        registerRecipes();
        registerListeners();
        atmManager.loadATMs();
        economyPlayerManager.loadOnlineEconomyPlayers();
        machineManager.loadMachines();
        npcDataManager.loadNpcShops();
    }

    @Override
    public void onDisable() {
        economyPlayerManager.saveOnlineEconomyPlayers();
        machineManager.saveMachines();
        npcDataManager.saveNpcShops();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
        }
    }

    private void createConfig() {
        saveDefaultConfig();
    }

    private void registerManagers() {
        walletManager = new WalletManager(this);
        atmManager = new ATMManager(this);
        machineManager = new MachineManager(this);
        economyPlayerManager = new EconomyPlayerManager(this);
        npcDataManager = new NPCDataManager(this);
    }

    private void registerCommands() {
        CommandExecutor commandExecutor = new CommandRunner(this);
        TabCompleter tabCompleter = new CommandCompleter(this);
        getCommand("economy").setExecutor(commandExecutor);
        getCommand("economy").setTabCompleter(tabCompleter);
        getCommand("linkmachine").setExecutor(commandExecutor);
        getCommand("linknpc").setExecutor(commandExecutor);
        getCommand("npcshop").setExecutor(commandExecutor);
        getCommand("npcshop").setTabCompleter(tabCompleter);
        economySubcommands.put("balance", new EconomyCommandBalance());
        economySubcommands.put("cash", new EconomyCommandCash());
        economySubcommands.put("wallet", new EconomyCommandWallet());
        economySubcommands.put("atm", new EconomyCommandATM());
        economySubcommands.put("currency", new EconomyCommandCurrency());
        economySubcommands.put("help", new EconomyCommandHelp());
        economySubcommands.put("reload", new EconomyCommandReload(this));
        npcSubcommands.put("create", new NPCCommandCreate(this));
        npcSubcommands.put("configure", new NPCCommandConfigure(this));
        npcSubcommands.put("delete", new NPCCommandDelete(this));
        npcSubcommands.put("link", new NPCCommandLink(this));
        npcSubcommands.put("equip", new NPCCommandEquip(this));
        npcSubcommands.put("tool", new NPCCommandTool());
    }

    private void registerRecipes() {
        Bukkit.addRecipe(WalletRecipe.getWalletRecipe());
        MachineRecipes machineRecipes = new MachineRecipes();
        Bukkit.addRecipe(machineRecipes.getVendingMachineRecipe());
        Bukkit.addRecipe(machineRecipes.getTradingMachineRecipe());
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerConnectionHandler(this), this);
        pluginManager.registerEvents(new WalletHandler(), this);
        pluginManager.registerEvents(new CurrencyGUIHandler(), this);
        pluginManager.registerEvents(new ATMHandler(this), this);
        pluginManager.registerEvents(new ShopHandler(this), this);
        pluginManager.registerEvents(new MachineHandler(this), this);
        pluginManager.registerEvents(new NPCSHopHandler(this), this);
    }

    public Map<String, EconomyCommand> getEconomySubcommands() {
        return economySubcommands;
    }

    public Map<Player, EconomyPlayer> getEconomyPlayers() {
        return economyPlayers;
    }

    public Map<Location, ATM> getAtms() {
        return atms;
    }

    public Map<Location, AbstractMachine> getMachines() {
        return machines;
    }

    public Map<UUID, AbstractMachine> getMachineLinkers() {
        return machineLinkers;
    }

    public Map<String, NPCCommand> getNpcSubcommands() {
        return npcSubcommands;
    }

    public Map<EntityPlayer, AbstractNPCShop> getNpcShops() {
        return npcShops;
    }

    public Map<UUID, NPCShopOperator> getNpcShopOperators() {
        return npcShopOperators;
    }

    public EconomyPlayerManager getEconomyPlayerManager() {
        return economyPlayerManager;
    }

    public WalletManager getWalletManager() {
        return walletManager;
    }

    public ATMManager getAtmManager() {
        return atmManager;
    }

    public MachineManager getMachineManager() {
        return machineManager;
    }

    public NPCDataManager getNpcDataManager() {
        return npcDataManager;
    }
}
