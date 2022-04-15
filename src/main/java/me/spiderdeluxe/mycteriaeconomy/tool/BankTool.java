package me.spiderdeluxe.mycteriaeconomy.tool;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.models.bank.Branch;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.tool.BlockTool;

/**
 * Handles tools that click within an branch.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BankTool<T extends Branch> extends BlockTool {


	/**
	 * Handles clicking with this tool, automatically fires {@link #onBlockClick(Player, Branch, ClickType, Block)}
	 * and only fires it when the player is editing something
	 *
	 * @see BlockTool#onBlockClick(Player, ClickType, Block)
	 */
	@Override
	protected final void onBlockClick(final Player player, final ClickType click, final Block block) {
		final EconomyPlayer economyPlayer = EconomyPlayer.from(player);


		final Branch branch = Branch.getEditingBranch(player);

		if (branch == null || branch.getEditingPlayer() != player) {
			Messenger.error(player, "If you want to use this tool, you must first do /bank menu and edit it.");

			return;
		}


		onBlockClick(player, (T) branch, click, block);
	}

	/**
	 * Automatically handle hotbar focus for while editing the branch
	 *
	 * @see org.mineacademy.fo.menu.tool.Tool#onHotbarFocused(Player)
	 */
	@Override
	protected final void onHotbarFocused(final Player player) {
		final Branch branch = getEditedBranch(player);

		if (branch != null)
			onHotbarFocused(player, (T) branch);
	}

	/**
	 * Automatically handle hotbar defocus for while editing the branch
	 *
	 * @see org.mineacademy.fo.menu.tool.Tool#onHotbarDefocused(Player)
	 */
	@Override
	protected final void onHotbarDefocused(final Player player) {
		final Branch branch = getEditedBranch(player);

		if (branch != null)
			onHotbarDefocused(player, (T) branch);

	}

	/**
	 * Handles a click when a player holding this tool clicks inside of their
	 *
	 * @param player
	 * @param branch
	 * @param click
	 * @param block
	 */
	protected abstract void onBlockClick(Player player, T branch, ClickType click, Block block);

	/**
	 * Called when the player that edits the given arena focuses his hotbar on this tool
	 *
	 * @param player
	 * @param branch
	 */
	protected void onHotbarFocused(final Player player, final T branch) {
	}

	/**
	 * Called when the player that edits the given arena defocuses his hotbar having this tool
	 *
	 * @param player
	 * @param branch
	 */
	protected void onHotbarDefocused(final Player player, final T branch) {
	}

	/**
	 * Called automatically when the player starts editing the given branch
	 *
	 * @param player
	 * @param branch
	 */
	public void onEditStart(final Player player, final T branch) {
	}

	/**
	 * Called automatically when the player stops editing the given branch
	 *
	 * @param player
	 * @param branch
	 */
	public void onEditStop(final Player player, final T branch) {
	}


	/*
	 * Return the branch that the player is currently editing
	 * or null if none
	 */
	private Branch getEditedBranch(final Player player) {
		return Branch.getEditingBranch(player);
	}

}
