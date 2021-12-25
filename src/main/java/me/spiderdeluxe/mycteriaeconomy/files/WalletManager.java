package me.spiderdeluxe.mycteriaeconomy.files;

import me.spiderdeluxe.mycteriaeconomy.MycteriaEconomyPlugin;
import me.spiderdeluxe.mycteriaeconomy.models.Wallet;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class WalletManager {
    private final MycteriaEconomyPlugin plugin;

    public WalletManager(final MycteriaEconomyPlugin plugin) {
        this.plugin = plugin;
    }

    // Saves the passed Wallet to its corresponding .yml file
    public void saveWallet(final Wallet wallet) {
        final WalletFile walletFile = new WalletFile(plugin, wallet);
        final FileConfiguration walletData = walletFile.getData();
        walletData.set("balance", wallet.getBalance());
        walletData.set("content", wallet.getGUI().getContents());
        walletFile.saveData();
    }

    // Loads the Wallet with the passed UUID from its corresponding .yml file
    public Wallet loadWallet(final UUID walletUuid) {
        final WalletFile walletFile = new WalletFile(plugin, walletUuid);
        final FileConfiguration walletData = walletFile.getData();
        if (walletData.getKeys(true).size() == 0) { // Wallet file is empty
            return new Wallet(walletUuid);
        }
        final double balance = walletData.getDouble("balance");
        final ItemStack[] content = walletData.getList("content").toArray(new ItemStack[0]);
        return new Wallet(walletUuid, balance, content);
    }
}
