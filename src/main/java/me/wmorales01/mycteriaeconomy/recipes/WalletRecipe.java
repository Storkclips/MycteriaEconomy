package me.wmorales01.mycteriaeconomy.recipes;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.Wallet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class WalletRecipe {

    public static ShapedRecipe getWalletRecipe() {
        MycteriaEconomy plugin = MycteriaEconomy.getPlugin(MycteriaEconomy.class);
        Wallet wallet = new Wallet();
        ItemStack result = wallet.getItemStack();
        NamespacedKey key = new NamespacedKey(plugin, "wallet");
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        recipe.shape("L L", "LPL", " L ");
        recipe.setIngredient('L', Material.LEATHER);
        recipe.setIngredient('P', Material.PAPER);

        return recipe;
    }
}
