package me.spiderdeluxe.mycteriaeconomy.models.shop.conversation;

import me.spiderdeluxe.mycteriaeconomy.models.shop.Shop;
import me.spiderdeluxe.mycteriaeconomy.models.shop.menu.ShopMenu;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.conversation.SimpleConversation;
import org.mineacademy.fo.conversation.SimplePrompt;

public class ShopRenamePrompt extends SimplePrompt {

	Shop promptShop;

	public ShopRenamePrompt(final Shop shop) {
		super(false);
		promptShop = shop;
	}

	@Override
	protected String getPrompt(final ConversationContext ctx) {
		return "&6Write the new display name of your shop.";
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input) {
		if(!Settings.General.SHOP_DISPLAY_NAME_USES_AMPERSAND)
		if (input.contains("&") || input.contains("%"))
			return false;
		return (input.length() <= Settings.General.SHOP_DISPLAY_NAME_MAX_LENGTH
				&& input.length() >= Settings.General.SHOP_DISPLAY_NAME_MIN_LENGTH);
	}

	@Override
	protected String getFailedValidationText(final ConversationContext context, final String invalidInput) {
		return "This name is invalid.";
	}

	@Override
	protected @Nullable
	Prompt acceptValidatedInput(@NotNull final ConversationContext context, @NotNull final String input) {
		promptShop.changeDisplayName(input);
		tell(context, "&6Now the display name of your shop is " + input + ".");
		return Prompt.END_OF_CONVERSATION;
	}

	@Override
	public void onConversationEnd(final SimpleConversation conversation, final ConversationAbandonedEvent event) {
		new ShopMenu.OwnerMenu(promptShop, new ShopMenu(promptShop)).displayTo(getPlayer(event.getContext()));
	}

}
