package me.spiderdeluxe.mycteriaeconomy.menu.shop;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.exception.FoException;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.InventoryDrawer;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.SimpleLocalization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An advanced menu listing items with automatic page support
 *
 * @param <T> the item that each page consists of
 */
public abstract class MenuShopPagged<T> extends Menu {

	/**
	 * The active page button material, used in buttons to previous/next pages
	 * when they can be clicked (such as to go to the next/previous page)
	 * <p>
	 * Defaults to lime dye
	 */
	@Getter
	@Setter
	private static CompMaterial activePageButton = CompMaterial.LIME_DYE;

	/**
	 * The inactive page button material, used in buttons to previous/next
	 * pages when they cannot be clicked (i.e. on the first/last page)
	 * <p>
	 * Defaults to gray dye
	 */
	@Getter
	@Setter
	private static CompMaterial inactivePageButton = CompMaterial.GRAY_DYE;

	/**
	 * The pages by the page number, containing a list of items
	 */
	@Getter
	private final Map<Integer, List<T>> pages;

	/**
	 *
	 */
	private List<T> nextPageItems;

	/**
	 * The current page
	 */
	@Getter
	private int currentPage = 1;

	/**
	 * The next button automatically generated
	 */
	private Button nextButton;

	/**
	 * The emptyItemButton automatically generated
	 */
	private Button emptyItemButton;

	/**
	 * The "go to previous page" button automatically generated
	 */
	private Button prevButton;

	/**
	 * Create a new paged menu where each page has 3 rows + 1 bottom bar
	 *
	 * @param pages the pages
	 */
	protected MenuShopPagged(final Iterable<T> pages) {
		this(null, pages);
	}

	/**
	 * Create a new paged menu
	 *
	 * @param parent the parent menu
	 * @param pages  the pages the pages
	 */
	protected MenuShopPagged(final Menu parent, final Iterable<T> pages) {
		this(null, parent, pages, false);
	}

	/**
	 * Create a new paged menu
	 *
	 * @param pageSize size of the menu, a multiple of 9 (keep in mind we already add
	 *                 1 row there)
	 * @param pages    the pages
	 * @deprecated we recommend you don't set the page size for the menu to
	 * autocalculate
	 */
	@Deprecated
	protected MenuShopPagged(final int pageSize, final Iterable<T> pages) {
		this(pageSize, null, pages);
	}

	/**
	 * Create a new paged menu
	 *
	 * @param pageSize size of the menu, a multiple of 9 (keep in mind we already add
	 *                 1 row there)
	 * @param parent   the parent menu
	 * @param pages    the pages the pages
	 * @deprecated we recommend you don't set the page size for the menu to
	 * autocalculate
	 */
	@Deprecated
	protected MenuShopPagged(final int pageSize, final Menu parent, final Iterable<T> pages) {
		this(pageSize, parent, pages, false);
	}

	/**
	 * Create a new paged menu
	 *
	 * @param pageSize
	 * @param parent
	 * @param pages
	 * @param returnMakesNewInstance *
	 * @deprecated we recommend you don't set the page size for the menu to
	 * autocalculate
	 */
	@Deprecated
	protected MenuShopPagged(final int pageSize, final Menu parent, final Iterable<T> pages, final boolean returnMakesNewInstance) {
		this((Integer) pageSize, parent, pages, returnMakesNewInstance);
	}

	/**
	 * Create a new paged menu
	 *
	 * @param pageSize               size of the menu, a multiple of 9 (keep in mind we already add
	 *                               1 row there)
	 * @param parent                 the parent menu
	 * @param pages                  the pages the pages
	 * @param returnMakesNewInstance should we re-instatiate the parent menu when returning to it?
	 */
	private MenuShopPagged(final Integer pageSize, final Menu parent, final Iterable<T> pages, final boolean returnMakesNewInstance) {
		super(parent, returnMakesNewInstance);

		final int items = getItemAmount(pages);
		final int autoPageSize = pageSize != null ? pageSize : items <= 9 ? (9 * 1) : items <= 9 * 2 ? (9 * 2) : items <= 9 * 3 ? (9 * 3) : items <= 9 * 4 ? 9 * 4 : (9 * 5);

		this.currentPage = 1;
		this.pages = fillPages(autoPageSize, pages);

		setSize(9 + autoPageSize);
		setButtons();
	}


	/**
	 * Dynamically populates pages, used for pagination in commands or menus
	 *
	 * @param items all items that will be split
	 * @return the map containing pages and their items
	 */
	public static <T> Map<Integer, List<T>> fillPages(int cellSize, final Iterable<T> items) {
		final List<T> allItems = Common.toList(items);

		switch (cellSize) {
			case 9:
				cellSize = 9 - 2;
				break;
			case 18:
				cellSize = 18 - 4;
				break;
			case 27:
				cellSize = 27 - 6;
				break;
			case 36:
				cellSize = 36 - 8;
		}

		final Map<Integer, List<T>> pages = new HashMap<>();
		final int pageCount = allItems.size() == cellSize ? 0 : allItems.size() / cellSize;

		for (int i = 0; i <= pageCount; i++) {
			final List<T> pageItems = new ArrayList<>();

			final int down = cellSize * i;
			final int up = down + cellSize;

			for (int valueIndex = down; valueIndex < up; valueIndex++)
				if (valueIndex < allItems.size()) {
					final T page = allItems.get(valueIndex);

					pageItems.add(page);
				} else
					break;

			pages.put(i, pageItems);
		}
		return pages;
	}

	private int getItemAmount(final Iterable<T> pages) {
		int amount = 0;

		for (final T t : pages)
			amount++;

		return amount;
	}


	// Render the next/prev buttons
	private void setButtons() {
		final boolean hasPages = pages.size() > 1;

		// Set previous button
		prevButton = hasPages ? formPreviousButton() : Button.makeEmpty();

		emptyItemButton = formEmptyItemButton();

		// Set next page button
		nextButton = hasPages ? formNextButton() : Button.makeEmpty();
	}

	/**
	 * Return the button to list the previous page,
	 * override to customize it.
	 *
	 * @return
	 */
	public Button formPreviousButton() {
		return new Button() {
			final boolean canGo = currentPage > 1;

			@Override
			public void onClickedInMenu(final Player pl, final Menu menu, final ClickType click) {
				if (canGo) {
					currentPage = MathUtil.range(currentPage - 1, 1, pages.size());

					updatePage();
				}
			}

			@Override
			public ItemStack getItem() {
				final int previousPage = currentPage - 1;

				return ItemCreator
						.of(canGo ? activePageButton : inactivePageButton)
						.name(previousPage == 0 ? SimpleLocalization.Menu.PAGE_FIRST : SimpleLocalization.Menu.PAGE_PREVIOUS.replace("{page}", String.valueOf(previousPage)))
						.build().make();
			}
		};
	}

	/**
	 * Return the button to list the next page,
	 * override to customize it.
	 *
	 * @return
	 */
	public Button formNextButton() {
		return new Button() {
			final boolean canGo = currentPage < pages.size();

			@Override
			public void onClickedInMenu(final Player pl, final Menu menu, final ClickType click) {
				if (canGo) {
					currentPage = MathUtil.range(currentPage + 1, 1, pages.size());

					updatePage();
				}
			}

			@Override
			public ItemStack getItem() {
				final boolean lastPage = currentPage == pages.size();

				return ItemCreator
						.of(canGo ? activePageButton : inactivePageButton)
						.name(lastPage ? SimpleLocalization.Menu.PAGE_LAST : SimpleLocalization.Menu.PAGE_NEXT.replace("{page}", String.valueOf(currentPage + 1)))
						.build().make();
			}
		};
	}

	/**
	 * Return the button to list the next page,
	 * override to customize it.
	 *
	 * @return
	 */
	public Button formEmptyItemButton() {
		return null;
	}

	// Reinits the menu and plays the anvil sound
	private void updatePage() {
		setButtons();
		redraw();
		registerButtons();

		Menu.getSound().play(getViewer());
		PlayerUtil.updateInventoryTitle(getViewer(), compileTitle0());
	}

	// Compile title and page numbers
	private String compileTitle0() {
		final boolean canAddNumbers = addPageNumbers() && pages.size() > 1;

		return getTitle() + (canAddNumbers ? " &8" + currentPage + "/" + pages.size() : "");
	}

	/**
	 * Automatically prepend the title with page numbers
	 * <p>
	 * Override for a custom last-minute implementation, but
	 * ensure to call the super method otherwise no title will
	 * be set in {@link InventoryDrawer}
	 *
	 * @param
	 */
	@Override
	protected final void onDisplay(final InventoryDrawer drawer) {
		drawer.setTitle(compileTitle0());
	}

	/**
	 * Return the {@link ItemStack} representation of an item on a certain page
	 * <p>
	 * Use {@link ItemCreator} for easy creation.
	 *
	 * @param item the given object, for example Arena
	 * @return the itemstack, for example diamond sword having arena name
	 */
	protected abstract ItemStack convertToItemStack(T item);

	/**
	 * Called automatically when an item is clicked
	 *
	 * @param player the player who clicked
	 * @param item   the clicked item
	 * @param click  the click type
	 */
	protected abstract void onPageClick(Player player, T item, ClickType click);

	/**
	 * Utility: Shall we send update packet when the menu is clicked?
	 *
	 * @return true by default
	 */
	protected boolean updateButtonOnClick() {
		return true;
	}

	/**
	 * Return true if you want our system to add page/totalPages suffix after
	 * your title, true by default
	 *
	 * @return
	 */
	protected boolean addPageNumbers() {
		return true;
	}

	/**
	 * Return if there are no items at all
	 *
	 * @return
	 */
	protected boolean isEmpty() {
		return pages.isEmpty() || pages.get(0).isEmpty();
	}


	protected boolean isLeftBorderSlot(final int slot) {
		return List.of(0, 9, 18, 27).contains(slot);
	}

	protected boolean isRightBorderSlot(final int slot) {
		return List.of(8, 17, 26, 35).contains(slot);
	}


	/**
	 * Automatically get the correct item from the actual page, including
	 * prev/next buttons
	 *
	 * @param slot the slot
	 * @return the item, or null
	 */
	@Override
	public ItemStack getItemAt(final int slot) {
		T object;


		if (slot == this.getPreviousButtonPosition())
			return prevButton.getItem();

		if (slot == this.getNextButtonPosition())
			return nextButton.getItem();


		for (int i = 0; i < 5; i++) {

			if (getCurrentPageItems().size() <= slot - (slot < 9 ? 1
					: slot < 18 ? 3
					: slot < 26 ? 5
					: 7) ) {
				if (!isRightBorderSlot(slot)
						&& !isLeftBorderSlot(slot)) {
					if (slot < 35) {
						return emptyItemButton.getItem();
					}
					return null;
				} else
					return null;
			}

			if (!isRightBorderSlot(slot)
					&& !isLeftBorderSlot(slot)) {
				if (9 > slot) {
					object = getCurrentPageItems().get(slot - 1);
					if (object != null)
						return convertToItemStack(object);
				} else if (9 * (i + 1) > slot && 9 * (i) < slot) {

					object = getCurrentPageItems().get(slot - i * 3 + (i - 1));
					if (object != null)
						return convertToItemStack(object);
				}
			}
		}

		return null;
	}

	/**
	 * Override to edit where the button to previous page is,
	 * defaults to "size of the menu - 6"
	 *
	 * @return
	 */
	protected int getPreviousButtonPosition() {
		return this.getSize() - 6;
	}

	/**
	 * Override to edit where the button to next page is,
	 * defaults to "size of the menu - 4"
	 *
	 * @return
	 */
	protected int getNextButtonPosition() {
		return this.getSize() - 4;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void onMenuClick(final Player player, final int slot, final InventoryAction action, final ClickType click, final ItemStack cursor, final ItemStack clicked, final boolean cancelled) {
		T obj;

		for (int i = 0; i < 5; i++) {
			if (getCurrentPageItems().size() <= slot - (slot < 9 ? 1
					: slot < 18 ? 3
					: slot < 26 ? 5
					: 7) ) {
				continue;
			}

			if (!isRightBorderSlot(slot)
					&& !isLeftBorderSlot(slot)) {
				if (9 > slot) {
					obj = getCurrentPageItems().get(slot - 1);
					if (obj != null) {
						val prevType = player.getOpenInventory().getType();
						onPageClick(player, obj, click);

						if (updateButtonOnClick() && prevType == player.getOpenInventory().getType())
							player.getOpenInventory().getTopInventory().setItem(slot, getItemAt(slot));
						return;
					}
				} else if (9 * (i + 1) > slot && 9 * (i) < slot) {

					obj = getCurrentPageItems().get(slot - i * 3 + (i - 1));
					if (obj != null) {
						val prevType = player.getOpenInventory().getType();
						onPageClick(player, obj, click);

						if (updateButtonOnClick() && prevType == player.getOpenInventory().getType())
							player.getOpenInventory().getTopInventory().setItem(slot, getItemAt(slot));
						return;
					}
				}

			}
		}
	}

	// Do not allow override
	@Override
	public void onButtonClick(final Player player, final int slot, final InventoryAction action, final ClickType click, final Button button) {
		super.onButtonClick(player, slot, action, click, button);
	}

	// Do not allow override
	@Override
	public final void onMenuClick(final Player player, final int slot, final ItemStack clicked) {
		throw new FoException("Simplest click unsupported");
	}

	// Get all items in a page
	private List<T> getCurrentPageItems() {
		Valid.checkBoolean(pages.containsKey(currentPage - 1), "The menu has only " + pages.size() + " pages, not " + currentPage + "!");


		return pages.get(currentPage - 1);
	}
}