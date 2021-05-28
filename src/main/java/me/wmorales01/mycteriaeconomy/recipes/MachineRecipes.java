package me.wmorales01.mycteriaeconomy.recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.models.TradingMachine;
import me.wmorales01.mycteriaeconomy.models.VendingMachine;

public class MachineRecipes {
	
	public ShapedRecipe getVendingMachineRecipe() {
		MycteriaEconomy plugin = MycteriaEconomy.getPlugin(MycteriaEconomy.class);
		ItemStack result = VendingMachine.getItemStack();
		NamespacedKey key = new NamespacedKey(plugin, "vending_machine");
		ShapedRecipe recipe = new ShapedRecipe(key, result);
		
		recipe.shape("IRI", "ICI", "IDI");
		recipe.setIngredient('I', Material.IRON_INGOT);
		recipe.setIngredient('R', Material.COMPARATOR);
		recipe.setIngredient('C', Material.CHEST);
		recipe.setIngredient('D', Material.IRON_DOOR);
		
		return recipe;
	}
	
	public ShapedRecipe getTradingMachineRecipe() {
		MycteriaEconomy plugin = MycteriaEconomy.getPlugin(MycteriaEconomy.class);
		ItemStack result = TradingMachine.getItemStack();
		NamespacedKey key = new NamespacedKey(plugin, "trading_machine");
		ShapedRecipe recipe = new ShapedRecipe(key, result);
		
		recipe.shape("IRI", "ICI", "IDI");
		recipe.setIngredient('I', Material.IRON_INGOT);
		recipe.setIngredient('R', Material.REPEATER);
		recipe.setIngredient('C', Material.CHEST);
		recipe.setIngredient('D', Material.IRON_DOOR);
		
		return recipe;
	}
}
