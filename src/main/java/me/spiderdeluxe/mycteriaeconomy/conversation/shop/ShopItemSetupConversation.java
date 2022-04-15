package me.spiderdeluxe.mycteriaeconomy.conversation.shop;

import me.spiderdeluxe.mycteriaeconomy.models.shop.ItemShop;
import me.spiderdeluxe.mycteriaeconomy.models.shop.Shop;
import me.spiderdeluxe.mycteriaeconomy.menu.shop.ShopMenu;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.conversation.SimpleConversation;
import org.mineacademy.fo.conversation.SimplePrefix;
import org.mineacademy.fo.conversation.SimplePrompt;

public class ShopItemSetupConversation extends SimpleConversation {
	ItemShop promptItem;
	Shop promptShop;

	public ShopItemSetupConversation(final Shop shop, final ItemShop item) {
		promptItem = item;
		promptShop = shop;

	}

	@Override
	protected Prompt getFirstPrompt() {
		return new QuantityPrompt(promptShop, promptItem);
	}

	@Override
	protected SimplePrefix getPrefix() {
		return new SimplePrefix("&8[&6Item Manager&8]&7 ");
	}

	@Override
	protected void onConversationEnd(final ConversationAbandonedEvent event) {
		if (!event.gracefulExit())
			tell(event.getContext().getForWhom(), "Your item's modification process has been cancelled.");
	}

	private static class QuantityPrompt extends SimplePrompt {
		ItemShop promptItem;
		Shop promptShop;

		public QuantityPrompt(final Shop shop, final ItemShop item) {
			promptItem = item;
			promptShop = shop;
		}

		@Override
		protected String getPrompt(final ConversationContext ctx) {
			return "Enter the amount of items that will be sold each time the player makes a purchase";
		}

		@Override
		protected boolean isInputValid(final ConversationContext context, final String input) {
			if (!Valid.isInteger(input))
				return false;

			try {
				final int quantity = Integer.parseInt(input);
				return quantity > 0;
			} catch (final NumberFormatException formatException) {
				return false;
			}
		}

		@Override
		protected String getFailedValidationText(final ConversationContext context, final String invalidInput) {
			return "You must specify a valid number that is greater than 0";
		}

		@Override
		protected @Nullable Prompt acceptValidatedInput(@NotNull final ConversationContext context, @NotNull final String input) {
			final int quantity = Integer.parseInt(input);

			promptShop.editItemQuantity(promptItem, quantity);

			return new PricePrompt(promptShop, promptItem);
		}

	}

	private static class PricePrompt extends SimplePrompt {

		ItemShop promptItem;
		Shop promptShop;

		public PricePrompt(final Shop shop, final ItemShop item) {
			promptItem = item;
			promptShop = shop;
		}

		@Override
		protected String getPrompt(final ConversationContext ctx) {
			return "Enter the price at which this item will be sold!";
		}

		@Override
		protected boolean isInputValid(final ConversationContext context, final String input) {
			if (!Valid.isDecimal(input) && !Valid.isInteger(input))
				return false;
			try {
				final double price = Double.parseDouble(input);
				return price > 0;
			} catch (final NumberFormatException formatException) {
				return false;
			}
		}

		@Override
		protected String getFailedValidationText(final ConversationContext context, final String invalidInput) {
			return "You must specify a valid number that is greater than 0";
		}

		@Override
		protected @Nullable Prompt acceptValidatedInput(@NotNull final ConversationContext context, @NotNull final String input) {
			final double price = Double.parseDouble(input);

			promptShop.editItemPrice(promptItem, price);

			return Prompt.END_OF_CONVERSATION;
		}

		@Override
		public void onConversationEnd(final SimpleConversation conversation, final ConversationAbandonedEvent event) {
			final Player player = getPlayer(event.getContext());
			tell(player, "Your shop item has been successfully modified!");
			new ShopMenu(promptShop).displayTo(player);
		}
	}


}