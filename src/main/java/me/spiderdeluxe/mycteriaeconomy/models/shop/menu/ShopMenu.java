package me.spiderdeluxe.mycteriaeconomy.models.shop.menu;

import me.spiderdeluxe.mycteriaeconomy.models.shop.*;
import me.spiderdeluxe.mycteriaeconomy.models.shop.conversation.ShopItemAmountPrompt;
import me.spiderdeluxe.mycteriaeconomy.models.shop.conversation.ShopItemDestroyPrompt;
import me.spiderdeluxe.mycteriaeconomy.models.shop.conversation.ShopItemSetupConversation;
import me.spiderdeluxe.mycteriaeconomy.models.shop.conversation.ShopRenamePrompt;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.SimpleSound;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.Arrays;
import java.util.UUID;

public class ShopMenu extends MenuShopPagged<ItemShop> {

	private final Shop shop;

	private boolean hasPermissions;

	private final Button ownerSettings;

	public ShopMenu(final Shop shopMenu) {
		super(9 * 4, null, shopMenu.getItems());
		shop = shopMenu;
		shopMenu.updateShopItemsStock();
		setSound(new SimpleSound(Sound.valueOf(Settings.General.SHOP_ACTIVATION_SOUND),
				Settings.General.SHOP_ACTIVATION_VOLUME.floatValue(),
				Settings.General.SHOP_ACTIVATION_PITCH.floatValue()));

		setTitle("&8" + shopMenu.getDisplayName());
		getInfo();


		ownerSettings = new Button() {

			@Override
			public void onClickedInMenu(final Player pl, final Menu menu, final ClickType click) {
				new OwnerMenu(shop, getMenu(pl)).displayTo(pl);
			}


			@Override
			public ItemStack getItem() {

				return ItemCreator
						.of(CompMaterial.COMPASS)
						.name("Owner Settings")
						.lore("Use this button to edit the settings of this Shop")
						.build().make();
			}
		};
	}

	@Override
	public Button formEmptyItemButton() {
		return new Button() {


			@Override
			public void onClickedInMenu(final Player player, final Menu menu, final ClickType click) {
			}

			@Override
			public ItemStack getItem() {

				return ItemCreator
						.of(CompMaterial.YELLOW_STAINED_GLASS_PANE)
						.name("Empty Item")
						.lore("")
						.build().make();
			}
		};
	}

	@Override
	protected ItemStack convertToItemStack(final ItemShop item) {
		hasPermissions = Shop.hasAdminPermissions(shop, getViewer().getPlayer());

		if (ShopListener.isCustomizer(getViewer())
				&& hasPermissions) {
			return item.getConfigurationItem();
		} else {
			return item.getExhibitionItem(shop.getType() == ShopType.VENDING);
		}
	}

	@Override
	protected void onPageClick(final Player player, final ItemShop item, final ClickType click) {
		hasPermissions = Shop.hasAdminPermissions(shop, player);


		if (ShopListener.isCustomizer(player)
				&& hasPermissions) {
			if (click.isRightClick()) {
				new ShopItemDestroyPrompt(item, shop).show(player);
			} else
				new ShopItemSetupConversation(shop, item).start(player);
		} else {
			final int transactionAmount;

			if (click.isRightClick()) {
				if (item.getStock() > 0)
					new ShopItemAmountPrompt(shop, item).show(player);
				return;
			} else if (click.isLeftClick()) {
				transactionAmount = item.getQuantity();
			} else
				return;

			if (shop.getType() == ShopType.VENDING) {
				shop.buyShopItem(player, item, transactionAmount);
			} else {
				shop.sellShopItem(player, item, transactionAmount);
			}
		}
	}

	@Override
	public final void onButtonClick(final Player player, final int slot, final InventoryAction action, final ClickType click, final Button button) {
		hasPermissions = Shop.hasAdminPermissions(shop, player);

		if (hasPermissions)
			if (button.getItem().isSimilar(formEmptyItemButton().getItem())) {
				if (!ShopListener.isEditor(player)) {
					Common.tell(player, "You have correctly entered the add item mode");
					ShopListener.addEditor(player);
				} else {
					Common.tell(player, "You have correctly exited the add item mode");
					ShopListener.removeEditor(player);
				}
			}
		super.onButtonClick(player, slot, action, click, button);
	}


	@Override
	protected String[] getInfo() {
		return new String[]{
				"&6&lShop's information",
				"&eCreation date: &f" + shop.getCreationDate()

		};
	}

	@Override
	public ItemStack getItemAt(final int slot) {
		hasPermissions = Shop.hasAdminPermissions(shop, getViewer());

		if (slot == 44
				&& hasPermissions) {
			return ownerSettings.getItem();
		}
		return super.getItemAt(slot);
	}

	@Override
	protected void onMenuClose(final Player player, final Inventory inventory) {
		if (ShopListener.isEditor(player))
			ShopListener.removeEditor(player);

		player.playSound(player.getLocation(),
				Sound.valueOf(Settings.General.SHOP_DEACTIVATION_SOUND),
				Settings.General.SHOP_DEACTIVATION_VOLUME.floatValue(),
				Settings.General.SHOP_DEACTIVATION_PITCH.floatValue());

		super.onMenuClose(player, inventory);
	}

	// --------------------------------------------------------------------------------------------------------------
	// Utility Methods
	// --------------------------------------------------------------------------------------------------------------

	public void addNewItem(final ItemStack item) {
		final Player player = getViewer();

		final UUID uuid = Shop.createItemUUID(shop);

		final ItemShop itemShop = shop.addItem(uuid, item);

		if (itemShop != null) {
			new ShopItemSetupConversation(shop, itemShop).start(player);
		}
		Common.tell(player, "Congratulation!! you have added a new item to your shop");
	}


	// --------------------------------------------------------------------------------------------------------------
	// Owner menus
	// --------------------------------------------------------------------------------------------------------------
	public static class OwnerMenu extends Menu {
		Button selectVendingMode;
		Button renameShop;
		Button editItemShop;
		Button collectProfit;

		Shop shop;

		public OwnerMenu(final Shop shopMenu, final Menu parent) {
			super(parent);
			shop = shopMenu;
			setSize(9);
			setTitle("Owner menu");

			addReturnButton();
			selectVendingMode = new Button() {


				@Override
				public void onClickedInMenu(final Player player, final Menu menu, final ClickType click) {

					if (click != ClickType.LEFT)
						return;

					shopMenu.changeType(shopMenu.getType() == ShopType.VENDING ?
							ShopType.TRADING : ShopType.VENDING);

					restartMenu();
				}

				@Override
				public ItemStack getItem() {
					return ItemCreator
							.of(CompMaterial.BOOK)
							.name("Shop type")
							.lores(Arrays.asList("Click on this button to decide whether",
									"items are sold or purchased by customers",
									"&7Type: &r" + shopMenu.getType().getStringMessage()))
							.build().make();
				}


			};
			renameShop = new Button() {


				@Override
				public void onClickedInMenu(final Player player, final Menu menu, final ClickType click) {

					if (click != ClickType.LEFT)
						return;

					new ShopRenamePrompt(shopMenu).show(player);
				}

				@Override
				public ItemStack getItem() {
					return ItemCreator
							.of(CompMaterial.SPRUCE_SIGN)
							.name("Shop DisplayName")
							.lores(Arrays.asList("Click on this button to change the",
									"displayName of this shop",
									"&7DisplayName: &r" + shopMenu.getDisplayName()))
							.build().make();
				}


			};

			editItemShop = new Button() {


				@Override
				public void onClickedInMenu(final Player player, final Menu menu, final ClickType click) {

					if (click != ClickType.LEFT)
						return;

					if (ShopListener.isCustomizer(player))
						ShopListener.removeCustomizer(player);
					else
						ShopListener.addCustomizer(player);

					restartMenu();
				}

				@Override
				public ItemStack getItem() {
					return ItemCreator
							.of((ShopListener.isCustomizer(getViewer()) ?
									CompMaterial.GREEN_STAINED_GLASS :
									CompMaterial.RED_STAINED_GLASS))
							.name("Edit Shop Item")
							.lores(Arrays.asList("Click on this button to active",
									"edit item modality.",
									"&7Status: &r" + (ShopListener.isCustomizer(getViewer()) ?
											"&a&lACTIVE" :
											"&c&lDISABLED")))
							.build().make();
				}
			};

			collectProfit = new Button() {
				@Override
				public void onClickedInMenu(final Player player, final Menu menu, final ClickType click) {

					if (click != ClickType.LEFT)
						return;

					shop.collectShopProfit(player);
					restartMenu();
				}

				@Override
				public ItemStack getItem() {
					return ItemCreator
							.of(CompMaterial.SUNFLOWER)
							.name("Collect Profit")
							.lores(Arrays.asList("Click on this button to collect",
									"all shop's profit.",
									"&7Profit:&r " + shopMenu.getProfit()))
							.build().make();
				}
			};
		}

		@Override
		public ItemStack getItemAt(final int slot) {

			if (slot == 1)
				return selectVendingMode.getItem();
			if (slot == 2)
				return renameShop.getItem();
			if (slot == 3)
				return editItemShop.getItem();
			if (slot == 4 && shop.getOwner() == ShopOwnerType.PLAYER)
				return collectProfit.getItem();

			return super.getItemAt(slot);
		}
	}


}
