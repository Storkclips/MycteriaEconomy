package me.wmorales01.mycteriaeconomy.files;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.Wallet;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class WalletManager {
    private final MycteriaEconomy plugin;

    public WalletManager(MycteriaEconomy plugin) {
        this.plugin = plugin;
    }

    // Saves the passed Wallet to its corresponding .yml file
    public void saveWallet(Wallet wallet) {
        WalletFile walletFile = new WalletFile(plugin, wallet);
        FileConfiguration walletData = walletFile.getData();
        walletData.set("balance", wallet.getBalance());
        walletData.set("content", wallet.getGUI().getContents());
        walletFile.saveData();
    }

    // Loads the Wallet with the passed UUID from its corresponding .yml file
    public Wallet loadWallet(UUID walletUuid) {
        WalletFile walletFile = new WalletFile(plugin, walletUuid);
        FileConfiguration walletData = walletFile.getData();
        if (walletData.getKeys(true).size() == 0) { // Wallet file is empty
            return new Wallet(walletUuid);
        }
        double balance = walletData.getDouble("balance");
        ItemStack[] content = walletData.getList("content").toArray(new ItemStack[0]);
        return new Wallet(walletUuid, balance, content);
    }
}
