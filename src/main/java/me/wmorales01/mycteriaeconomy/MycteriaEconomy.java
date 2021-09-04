package me.wmorales01.mycteriaeconomy;

import me.wmorales01.mycteriaeconomy.commands.*;
import me.wmorales01.mycteriaeconomy.files.ATMManager;
import me.wmorales01.mycteriaeconomy.files.EconomyPlayerManager;
import me.wmorales01.mycteriaeconomy.files.NPCDataManager;
import me.wmorales01.mycteriaeconomy.files.WalletManager;
import me.wmorales01.mycteriaeconomy.listeners.*;
import me.wmorales01.mycteriaeconomy.models.*;
import me.wmorales01.mycteriaeconomy.recipes.MachineRecipes;
import me.wmorales01.mycteriaeconomy.recipes.WalletRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class MycteriaEconomy extends JavaPlugin {
    private final Map<String, EconomyCommand> economySubcommands = new LinkedHashMap<>();
    private final Map<String, NPCCommand> npcSubcommands = new HashMap<>();
    private final Map<Player, EconomyPlayer> economyPlayers = new HashMap<>();
    private final Map<Location, ATM> atms = new HashMap<>();
    private final Map<Player, Machine> machineLinkers = new HashMap<>();
    private final Map<Player, Chest> npcLinkers = new HashMap<>();
    private final List<VendingMachine> vendingMachines = new ArrayList<>();
    private final List<TradingMachine> tradingMachines = new ArrayList<>();
    private final List<MachineOperator> vendingOperators = new ArrayList<>();
    private final List<NPCShop> npcShops = new ArrayList<>();
    private final List<NPCOperator> npcOperators = new ArrayList<>();
    private WalletManager walletManager;
    private ATMManager atmManager;
    //    private MachineManager machineManager;
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
//        machineManager.loadAllMachines();
        economyPlayerManager.loadOnlineEconomyPlayers();
        npcDataManager.restoreNpcData();
        NPCManager npcManager = new NPCManager(this);
        PacketManager packetManager = new PacketManager(this);
        for (Player online : Bukkit.getOnlinePlayers()) {
            npcManager.addJoinPacket(online);
            packetManager.injectPacket(online);
        }
    }

    @Override
    public void onDisable() {
//        machineManager.saveAllMachines();
        economyPlayerManager.saveOnlineEconomyPlayers();
        npcDataManager.saveAllNPCs();

        PacketManager packetManager = new PacketManager(this);
        NPCManager npcManager = new NPCManager(this);
        for (Player player : Bukkit.getOnlinePlayers())
            player.closeInventory();
        for (Player online : Bukkit.getOnlinePlayers())
            packetManager.uninjectPacket(online);
        for (NPCShop shop : npcShops)
            npcManager.deleteNPC(shop, false);
    }

    private void createConfig() {
        saveDefaultConfig();
    }

    private void registerManagers() {
        walletManager = new WalletManager(this);
        atmManager = new ATMManager(this);
//        machineManager = new MachineManager(this);
        economyPlayerManager = new EconomyPlayerManager(this);
        npcDataManager = new NPCDataManager(this);
    }

    private void registerCommands() {
        CommandExecutor mainExecutor = new CommandRunner(this);
        getCommand("linkmachine").setExecutor(mainExecutor);
        getCommand("linknpc").setExecutor(mainExecutor);
        getCommand("npcshop").setExecutor(mainExecutor);
        getCommand("cash").setExecutor(new CashCommand(this));
        getCommand("wallet").setExecutor(new WalletCommand(this));
        getCommand("gui").setExecutor(new GUICommand());
        getCommand("atm").setExecutor(new CreateATMCommand(this));
        getCommand("setbalance").setExecutor(new SetBalanceCommand(this));
        getCommand("npcshop").setTabCompleter(new NPCCommandCompleter(this));
        npcSubcommands.put("create", new NPCCommandCreate(this));
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
        pluginManager.registerEvents(new CreativeGUIClick(), this);
        pluginManager.registerEvents(new ATMHandler(this), this);
//        pluginManager.registerEvents(new MachineHandler(this), this);
        pluginManager.registerEvents(new StockGUIClose(), this);
        pluginManager.registerEvents(new StockGUIClick(), this);
        pluginManager.registerEvents(new PlayerChat(this), this);
        pluginManager.registerEvents(new PrepareWalletCraft(), this);
        pluginManager.registerEvents(new NPCShopHandler(this), this);
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

    public List<VendingMachine> getVendingMachines() {
        return vendingMachines;
    }

    public List<MachineOperator> getVendingOperators() {
        return vendingOperators;
    }

    public List<TradingMachine> getTradingMachines() {
        return tradingMachines;
    }

    public List<NPCShop> getNpcShops() {
        return npcShops;
    }

    public Map<String, NPCCommand> getNpcSubcommands() {
        return npcSubcommands;
    }

    public Map<Player, Machine> getMachineLinkers() {
        return machineLinkers;
    }

    public Map<Player, Chest> getNpcLinkers() {
        return npcLinkers;
    }

    public List<NPCOperator> getNpcOperators() {
        return npcOperators;
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

    public NPCDataManager getNpcDataManager() {
        return npcDataManager;
    }
}
