package me.spiderdeluxe.mycteriaeconomy.tool;

import me.spiderdeluxe.mycteriaeconomy.models.bank.Branch;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.visual.BlockVisualizer;
import org.mineacademy.fo.visual.VisualizedRegion;

import java.util.List;

/**
 * A class that can visualize selection of blocks in the bank
 *
 * @param <T>
 */
public abstract class VisualTool<T extends Branch> extends BankTool<T> {


	/**
	 * Handle block clicking and automatically refreshes rendering of visualized blocks
	 *
	 * @param player
	 * @param arena
	 * @param click
	 * @param block
	 */
	@Override
	protected final void onBlockClick(final Player player, final T arena, final ClickType click, final Block block) {
		// Remove old blocks
		stopVisualizing(arena, player);

		// Call the block handling, probably new blocks will appear
		handleBlockClick(player, arena, click, block);

		// Render the new blocks
		visualize(arena, player);
	}

	/**
	 * Handles block clicking. Any changes here will be reflected automatically in the visualization
	 *
	 * @param player
	 * @param branch
	 * @param click
	 * @param block
	 */
	protected abstract void handleBlockClick(Player player, T branch, ClickType click, Block block);


	@Override
	public final void onEditStart(final Player player, final T branch) {
		// Visualize for the current tool if matches
		if (hasToolInHand(player))
			Common.runLater(() -> visualize(branch, player));
	}


	@Override
	protected final void onHotbarFocused(final Player player, final T branch) {
		visualize(branch, player);
	}


	@Override
	public final void onEditStop(final Player player, final T branch) {
		stopVisualizing(branch, player);
	}

	@Override
	protected final void onHotbarDefocused(final Player player, final T branch) {
		stopVisualizing(branch, player);
	}

	/**
	 * Return a list of points we should render in this visualization
	 *
	 * @param branch
	 * @return
	 */
	protected abstract List<Location> getVisualizedPoints(T branch);

	/**
	 * Return a region that this tool should draw particles around
	 *
	 * @param branch
	 * @return
	 */
	protected VisualizedRegion getVisualizedRegion(final T branch) {
		return null;
	}

	/**
	 * Return the name above the glowing block for the given parameters
	 *
	 * @param block
	 * @param player
	 * @param branch
	 * @return
	 */
	protected abstract String getBlockName(Block block, Player player, T branch);

	/**
	 * Return the block mask for the given parameters
	 *
	 * @param block
	 * @param player
	 * @param branch
	 * @return
	 */
	protected abstract CompMaterial getBlockMask(Block block, Player player, T branch);

	/*
	 * Visualize the region and points if exist
	 */
	private void visualize(final T branch, final Player player) {
		final VisualizedRegion region = getVisualizedRegion(branch);

		if (region != null && region.isWhole())
			if (!region.canSeeParticles(player))
				region.showParticles(player);

		for (final Location location : getVisualizedPoints(branch)) {
			final Block block = location.getBlock();

			BlockVisualizer.visualize(block, getBlockMask(block, player, branch), getBlockName(block, player, branch));
		}
	}

	/*
	 * Stop visualizing region and points if they were so before
	 */
	private void stopVisualizing(final T branch, final Player player) {
		final VisualizedRegion region = getVisualizedRegion(branch);

		if (region != null && region.canSeeParticles(player))
			region.hideParticles(player);

		for (final Location location : getVisualizedPoints(branch)) {
			if (location == null) return;
			final Block block = location.getBlock();

			if (BlockVisualizer.isVisualized(block))
				BlockVisualizer.stopVisualizing(block);
		}
	}
}
