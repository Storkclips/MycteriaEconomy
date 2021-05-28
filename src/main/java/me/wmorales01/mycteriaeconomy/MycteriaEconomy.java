package me.wmorales01.mycteriaeconomy;

import me.wmorales01.mycteriaeconomy.commands.*;
import me.wmorales01.mycteriaeconomy.events.*;
import me.wmorales01.mycteriaeconomy.files.*;
import me.wmorales01.mycteriaeconomy.models.*;
import me.wmorales01.mycteriaeconomy.recipes.MachineRecipes;
import me.wmorales01.mycteriaeconomy.recipes.WalletRecipe;
import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class MycteriaEconomy extends JavaPlugin {
    private WalletData walletData;
    private WalletManager walletManager;
    private MachineData machineData;
    private MachineManager machineManager;
    private EconomyPlayerData ecoPlayerData;
    private EconomyPlayerManager ecoPlayerManager;
    private NPCData npcData;
    private NPCDataManager npcDataManager;

    private Map<UUID, Wallet> openWallets = new HashMap<>();
    private Map<String, NPCCommand> npcSubcommands = new HashMap<>();
    private Map<Player, Machine> machineLinkers = new HashMap<>();
    private Map<Player, Chest> npcLinkers = new HashMap<>();

    private Set<Wallet> wallets = new HashSet<>();

    private List<EconomyPlayer> economyPlayers = new ArrayList<>();
    private List<Player> ATMPlacers = new ArrayList<>();
    private List<Player> ATMBreakers = new ArrayList<>();
    private List<ATM> ATMs = new ArrayList<>();
    private List<VendingMachine> vendingMachines = new ArrayList<>();
    private List<TradingMachine> tradingMachines = new ArrayList<>();
    private List<MachineOperator> vendingOperators = new ArrayList<>();
    private List<NPCShop> npcs = new ArrayList<>();
    private List<NPCOperator> npcOperators = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        walletData = new WalletData(this);
        walletManager = new WalletManager(this);
        machineData = new MachineData(this);
        machineManager = new MachineManager(this);
        ecoPlayerData = new EconomyPlayerData(this);
        ecoPlayerManager = new EconomyPlayerManager(this);
        npcData = new NPCData(this);
        npcDataManager = new NPCDataManager(this);

        getCommand("cash").setExecutor(new CashCommand(this));
        getCommand("linkmachine").setExecutor(new CommandRunner(this));
        getCommand("linknpc").setExecutor(new CommandRunner(this));
        getCommand("wallet").setExecutor(new WalletCommand(this));
        getCommand("gui").setExecutor(new GUICommand());
        getCommand("createatm").setExecutor(new CreateATMCommand(this));
        getCommand("setbalance").setExecutor(new SetBalanceCommand(this));
        getCommand("npcshop").setExecutor(new CommandRunner(this));
        getCommand("npcshop").setTabCompleter(new NPCCommandCompleter(this));
        npcSubcommands.put("create", new NPCCommandCreate(this));
        npcSubcommands.put("tool", new NPCCommandTool());

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerConnection(this), this);
        pm.registerEvents(new WalletHandler(this), this);
        pm.registerEvents(new CreativeGUIClick(), this);
        pm.registerEvents(new ATMPlace(this), this);
        pm.registerEvents(new ATMBreak(this), this);
        pm.registerEvents(new ATMOpen(this), this);
        pm.registerEvents(new ATMGUIClick(), this);
        pm.registerEvents(new MachineHandler(this), this);
        pm.registerEvents(new StockGUIClose(), this);
        pm.registerEvents(new StockGUIClick(), this);
        pm.registerEvents(new PlayerChat(this), this);
        pm.registerEvents(new PrepareWalletCraft(), this);
        pm.registerEvents(new NPCShopHandler(this), this);

        Bukkit.addRecipe(WalletRecipe.getWalletRecipe());
        MachineRecipes machineRecipes = new MachineRecipes();
        Bukkit.addRecipe(machineRecipes.getVendingMachineRecipe());
        Bukkit.addRecipe(machineRecipes.getTradingMachineRecipe());

        walletManager.restoreWallets();
        machineManager.loadAllMachines();
        ecoPlayerManager.loadEconomyPlayers();

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
        ecoPlayerManager.saveEconomyPlayers();
        npcDataManager.saveAllNPCs();

        PacketManager packetManager = new PacketManager(this);
        NPCManager npcManager = new NPCManager(this);
        for (Player player : Bukkit.getOnlinePlayers())
            player.closeInventory();
        for (Player online : Bukkit.getOnlinePlayers())
            packetManager.uninjectPacket(online);
        for (NPCShop shop : npcs)
            npcManager.deleteNPC(shop, false);
    }

    public static MycteriaEconomy getInstance() {
        return MycteriaEconomy.getPlugin(MycteriaEconomy.class);
    }

    public FileConfiguration getWalletData() {
        return walletData.getConfig();
    }

    public void saveWalletData() {
        walletData.saveConfig();
    }

    public FileConfiguration getMachineData() {
        return machineData.getConfig();
    }

    public void saveMachineData() {
        machineData.saveConfig();
    }

    public FileConfiguration getEcoPlayerData() {
        return ecoPlayerData.getConfig();
    }

    public void saveEcoPlayerData() {
        ecoPlayerData.saveConfig();
    }

    public FileConfiguration getNPCData() {
        return npcData.getConfig();
    }

    public void saveNPCData() {
        npcData.saveConfig();
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

    public void addOpenWallet(UUID uuid, Wallet wallet) {
        openWallets.put(uuid, wallet);
    }

    public List<EconomyPlayer> getEconomyPlayers() {
        return economyPlayers;
    }

    public void addEconomyPlayers(EconomyPlayer economyPlayer) {
        economyPlayers.add(economyPlayer);
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

    public List<NPCShop> getNpcs() {
        return npcs;
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

    public NPCDataManager getNpcDataManager() {
        return npcDataManager;
    }
}
