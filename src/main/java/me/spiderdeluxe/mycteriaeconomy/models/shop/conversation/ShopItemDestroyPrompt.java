package me.spiderdeluxe.mycteriaeconomy.models.shop.conversation;

import me.spiderdeluxe.mycteriaeconomy.models.shop.ItemShop;
import me.spiderdeluxe.mycteriaeconomy.models.shop.Shop;
import me.spiderdeluxe.mycteriaeconomy.models.shop.menu.ShopMenu;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.conversation.SimpleConversation;
import org.mineacademy.fo.conversation.SimplePrompt;

public class ShopItemDestroyPrompt extends SimplePrompt {

	ItemShop promptItem;
	Shop promptShop;

	public ShopItemDestroyPrompt(final ItemShop item, final Shop shop) {
		super(false);
		promptShop = shop;
		promptItem = item;
	}

	@Override
	protected String getPrompt(final ConversationContext ctx) {
		return "&6Are you sure you want to delete this item? Write &a&lyes &r&6if you are sure or write &c&lno &r&6if you want to cancel the process";
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input) {

		return input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("no");
	}

	@Override
	protected String getFailedValidationText(final ConversationContext context, final String invalidInput) {
		return "You must write yes or no, other messages are not considered.";
	}

	@Override
	protected @Nullable
	Prompt acceptValidatedInput(@NotNull final ConversationContext context, @NotNull final String input) {
		if(input.equalsIgnoreCase("yes")) {
			promptShop.removeItem(promptItem);
			tell( "&6You have successfully deleted this Item!");
		} else {
			tell("&cThe operation has been successfully cancelled!");
		}
		return Prompt.END_OF_CONVERSATION;
	}

	@Override
	public void onConversationEnd(final SimpleConversation conversation, final ConversationAbandonedEvent event) {
		new ShopMenu(promptShop).displayTo(getPlayer(event.getContext()));
	}

}
