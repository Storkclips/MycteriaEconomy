package me.spiderdeluxe.mycteriaeconomy.recipes;

import me.spiderdeluxe.mycteriaeconomy.MycteriaEconomyPlugin;
import me.spiderdeluxe.mycteriaeconomy.models.machine.Machine;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class MachineRecipes {

	public static ShapedRecipe getMachineRecipe() {
		final MycteriaEconomyPlugin plugin = MycteriaEconomyPlugin.getPlugin(MycteriaEconomyPlugin.class);
		final ItemStack result = Machine.getItemStack();
		final NamespacedKey key = new NamespacedKey(plugin, "commercial_machine");
		final ShapedRecipe recipe = new ShapedRecipe(key, result);

		recipe.shape("IRI", "ICI", "IDI");
		recipe.setIngredient('I', Material.IRON_INGOT);
		recipe.setIngredient('R', Material.COMPARATOR);
		recipe.setIngredient('C', Material.CHEST);
		recipe.setIngredient('D', Material.IRON_DOOR);

		return recipe;
	}

}
