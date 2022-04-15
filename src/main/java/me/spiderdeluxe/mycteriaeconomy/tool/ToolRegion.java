package me.spiderdeluxe.mycteriaeconomy.tool;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.spiderdeluxe.mycteriaeconomy.models.bank.Branch;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.visual.VisualizedRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the tool used to create arena region for any branch
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ToolRegion extends VisualTool<Branch> {

	@Getter
	private static final Tool instance = new ToolRegion();


	@Override
	protected String getBlockName(final Block block, final Player player, final Branch branch) {
		return "[&aRegion point&f]";
	}


	@Override
	protected CompMaterial getBlockMask(final Block block, final Player player, final Branch branch) {
		return CompMaterial.EMERALD_BLOCK;
	}

	/**
	 * @see Tool#getItem()
	 */
	@Override
	public ItemStack getItem() {
		return ItemCreator.of(
						CompMaterial.EMERALD,
						"&lREGION TOOL",
						"",
						"Use to set region points",
						"for an edited branch.",
						"",
						"&b<< &fLeft click &7– &fPrimary",
						"&fRight click &7– &fSecondary &b>>")
				.build().makeMenuTool();
	}


	@Override
	protected void handleBlockClick(final Player player, final Branch branch, final ClickType click, final Block block) {

		final Location location = block.getLocation();
		final boolean primary = click == ClickType.LEFT;

		if (primary)
			branch.setRegion(location, null);
		else
			branch.setRegion(null, location);

		Messenger.success(player, "Set the " + (primary ? "primary" : "secondary") + " bank point.");
	}


	@Override
	protected List<Location> getVisualizedPoints(final Branch branch) {
		final List<Location> blocks = new ArrayList<>();
		final Region region = branch.getRegion();

		if (region != null) {
			if (region.getPrimary() != null)
				blocks.add(region.getPrimary());

			if (region.getSecondary() != null)
				blocks.add(region.getSecondary());
		}

		return blocks;
	}


	@Override
	protected VisualizedRegion getVisualizedRegion(final Branch branch) {
		return branch.getRegion();
	}

	@Override
	protected boolean autoCancel() {
		return true; // Cancel the event so that we don't destroy blocks when selecting them
	}
}
