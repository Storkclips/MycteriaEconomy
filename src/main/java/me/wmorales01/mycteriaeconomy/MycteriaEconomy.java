package me.wmorales01.mycteriaeconomy;

import me.wmorales01.mycteriaeconomy.commands.*;
import me.wmorales01.mycteriaeconomy.files.*;
import me.wmorales01.mycteriaeconomy.listeners.*;
import me.wmorales01.mycteriaeconomy.models.*;
import me.wmorales01.mycteriaeconomy.recipes.MachineRecipes;
import me.wmorales01.mycteriaeconomy.recipes.WalletRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class MycteriaEconomy extends JavaPlugin {
    private final Map<String, NPCCommand> npcSubcommands = new HashMap<>();
    private final Map<Player, EconomyPlayer> economyPlayers = new HashMap<>();
    private final Map<UUID, Wallet> openWallets = new HashMap<>();
    private final Map<Location, ATM> atms = new HashMap<>();
    private final Map<Player, Machine> machineLinkers = new HashMap<>();
    private final Map<Player, Chest> npcLinkers = new HashMap<>();
    private final Set<Wallet> wallets = new HashSet<>();
    private final List<Player> ATMPlacers = new ArrayList<>();
    private final List<Player> ATMBreakers = new ArrayList<>();
    private final List<ATM> ATMs = new ArrayList<>();
    private final List<VendingMachine> vendingMachines = new ArrayList<>();
    private final List<TradingMachine> tradingMachines = new ArrayList<>();
    private final List<MachineOperator> vendingOperators = new ArrayList<>();
    private final List<NPCShop> npcShops = new ArrayList<>();
    private final List<NPCOperator> npcOperators = new ArrayList<>();
    private WalletData walletData;
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
        configureFiles();
        registerManagers();
        registerCommands();
        registerListeners();

        Bukkit.addRecipe(WalletRecipe.getWalletRecipe());
        MachineRecipes machineRecipes = new MachineRecipes();
        Bukkit.addRecipe(machineRecipes.getVendingMachineRecipe());
        Bukkit.addRecipe(machineRecipes.getTradingMachineRecipe());

        walletManager.restoreWallets();
        machineManager.loadAllMachines();
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
        walletManager.saveWallets();
        machineManager.saveAllMachines();
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

    private void configureFiles() {
        saveDefaultConfig();
        walletData = new WalletData(this);
    }

    private void registerManagers() {
        walletManager = new WalletManager(this);
        atmManager = new ATMManager(this);
        atmManager.loadATMs();
        machineManager = new MachineManager(this);
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
        getCommand("createatm").setExecutor(new CreateATMCommand(this));
        getCommand("setbalance").setExecutor(new SetBalanceCommand(this));
        getCommand("npcshop").setTabCompleter(new NPCCommandCompleter(this));
        npcSubcommands.put("create", new NPCCommandCreate(this));
        npcSubcommands.put("tool", new NPCCommandTool());
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerConnectionHandler(this), this);
        pluginManager.registerEvents(new WalletHandler(this), this);
        pluginManager.registerEvents(new CreativeGUIClick(), this);
        pluginManager.registerEvents(new ATMHandler(this), this);
        pluginManager.registerEvents(new ATMBreak(this), this);
        pluginManager.registerEvents(new ATMGUIClick(), this);
        pluginManager.registerEvents(new MachineHandler(this), this);
        pluginManager.registerEvents(new StockGUIClose(), this);
        pluginManager.registerEvents(new StockGUIClick(), this);
        pluginManager.registerEvents(new PlayerChat(this), this);
        pluginManager.registerEvents(new PrepareWalletCraft(), this);
        pluginManager.registerEvents(new NPCShopHandler(this), this);
    }

    public FileConfiguration getWalletData() {
        return walletData.getConfig();
    }

    public void saveWalletData() {
        walletData.saveConfig();
    }

    public Set<Wallet> getWallets() {
        return wallets;
    }

    public void addWallet(Wallet wallet) {
        wallets.add(wallet);
    }

    public Map<UUID, Wallet> getOpenWallets() {
        return openWallets;
    }

    public Map<Location, ATM> getAtms() {
        return atms;
    }

    public List<Player> getATMPlacers() {
        return ATMPlacers;
    }

    public List<ATM> getATMs() {
        return ATMs;
    }

    public List<Player> getATMBreakers() {
        return ATMBreakers;
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

    public Map<Player, EconomyPlayer> getEconomyPlayers() {
        return economyPlayers;
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

    public NPCDataManager getNpcDataManager() {
        return npcDataManager;
    }

    public EconomyPlayerManager getEconomyPlayerManager() {
        return economyPlayerManager;
    }

    public ATMManager getAtmManager() {
        return atmManager;
    }
}
