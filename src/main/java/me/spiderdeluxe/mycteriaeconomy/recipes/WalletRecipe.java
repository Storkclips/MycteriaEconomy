package me.spiderdeluxe.mycteriaeconomy.recipes;

import me.spiderdeluxe.mycteriaeconomy.MycteriaEconomyPlugin;
import me.spiderdeluxe.mycteriaeconomy.models.Wallet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class WalletRecipe {

    public static ShapedRecipe getWalletRecipe() {
        final MycteriaEconomyPlugin plugin = MycteriaEconomyPlugin.getPlugin(MycteriaEconomyPlugin.class);
        final Wallet wallet = new Wallet();
        final ItemStack result = wallet.getItemStack();
        final NamespacedKey key = new NamespacedKey(plugin, "wallet");
        final ShapedRecipe recipe = new ShapedRecipe(key, result);

        recipe.shape("L L", "LPL", " L ");
        recipe.setIngredient('L', Material.LEATHER);
        recipe.setIngredient('P', Material.PAPER);

        return recipe;
    }
}
