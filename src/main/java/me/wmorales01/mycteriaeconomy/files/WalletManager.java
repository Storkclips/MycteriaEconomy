package me.wmorales01.mycteriaeconomy.files;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.Wallet;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class WalletManager {
    private MycteriaEconomy plugin;

    public WalletManager(MycteriaEconomy instance) {
        plugin = instance;
    }

    public void saveWallets() {
        FileConfiguration data = plugin.getWalletData();

        for (Wallet wallet : plugin.getWallets()) {
            UUID uuid = wallet.getUuid();
            double balance = wallet.getBalance();
            ItemStack[] content = wallet.getContent().getContents();

            data.set("wallets." + uuid.toString() + ".balance", balance);
            data.set("wallets." + uuid.toString() + ".content", content);
        }

        plugin.saveWalletData();
    }

    public void restoreWallets() {
        FileConfiguration data = plugin.getWalletData();
        ConfigurationSection section = data.getConfigurationSection("wallets");
        if (section == null)
            return;

        section.getKeys(false).forEach(uuid -> {
            double balance = data.getDouble("wallets." + uuid + ".balance");
            @SuppressWarnings("unchecked")
            ItemStack[] contents = ((List<ItemStack>) data.get("wallets." + uuid + ".content"))
                    .toArray(new ItemStack[0]);

            Inventory content = Bukkit.createInventory(null, 36);
            content.setContents(contents);
            Wallet wallet = new Wallet(UUID.fromString(uuid), balance, content);

            plugin.addWallet(wallet);
        });
    }
}
