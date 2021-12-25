package me.spiderdeluxe.mycteriaeconomy.models.shop.conversation;

import me.spiderdeluxe.mycteriaeconomy.models.shop.ItemShop;
import me.spiderdeluxe.mycteriaeconomy.models.shop.Shop;
import me.spiderdeluxe.mycteriaeconomy.models.shop.ShopType;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.conversation.SimplePrompt;

public class ShopItemAmountPrompt extends SimplePrompt {

	ItemShop promptItem;
	Shop promptShop;

	public ShopItemAmountPrompt(final Shop shop, final ItemShop item) {
		super(false);
		promptItem = item;
		promptShop = shop;
	}

	@Override
	protected String getPrompt(final ConversationContext ctx) {
		return "&6Enter the amount of items you want to " + (promptShop.getType() == ShopType.TRADING ? "sell." : "purchase.");
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input) {
		if (!Valid.isInteger(input))
			return false;

		try {
			final int  transactionQuantity = Integer.parseInt(input);
			return transactionQuantity <= promptItem.getStock();
		} catch (final NumberFormatException formatException) {
			return false;
		}
	}

	@Override
	protected String getFailedValidationText(final ConversationContext context, final String invalidInput) {
		return "Only specify a number inferior/equal to the number of item stock: " + promptItem.getStock();
	}

	@Override
	protected @Nullable
	Prompt acceptValidatedInput(@NotNull final ConversationContext context, @NotNull final String input) {
		final int transactionQuantity = Integer.parseInt(input) *  promptItem.getQuantity();
		tell(context, "&6Now you are going to " +
				(promptShop.getType() == ShopType.TRADING ? "sell" : "buy") + " "
				+ input + " " + promptItem.getName() + ".");
		if (promptShop.getType() == ShopType.VENDING) {
			promptShop.buyShopItem(getPlayer(context), promptItem, transactionQuantity);
		} else {
			promptShop.sellShopItem(getPlayer(context), promptItem, transactionQuantity);
		}
		return Prompt.END_OF_CONVERSATION;
	}
}
