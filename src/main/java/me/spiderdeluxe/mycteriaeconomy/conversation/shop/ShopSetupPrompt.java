package me.spiderdeluxe.mycteriaeconomy.conversation.shop;

import me.spiderdeluxe.mycteriaeconomy.models.machine.Machine;
import me.spiderdeluxe.mycteriaeconomy.models.shop.Shop;
import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import me.spiderdeluxe.mycteriaeconomy.util.SFXManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.conversation.SimplePrompt;

public class ShopSetupPrompt extends SimplePrompt {

Location placedLocation;
	public ShopSetupPrompt(final Location location) {
		super(false);
		placedLocation = location;
	}

	@Override
	protected String getPrompt(final ConversationContext ctx) {
		return "&6Write the name of the shop, get creative and find the one that suits you best!";
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input) {
		if (!Settings.General.SHOP_DISPLAY_NAME_USES_AMPERSAND)
			if (input.contains("&") || input.contains("%"))
				return false;
		return (!Shop.alreadyExist(input)
				&& input.length() <= Settings.General.SHOP_DISPLAY_NAME_MAX_LENGTH
				&& input.length() >= Settings.General.SHOP_DISPLAY_NAME_MIN_LENGTH);
	}

	@Override
	protected String getFailedValidationText(final ConversationContext context, final String invalidInput) {
		return "This name is invalid or already used.";
	}

	@Override
	protected @Nullable
	Prompt acceptValidatedInput(@NotNull final ConversationContext context, @NotNull final String input) {
		final Player player = getPlayer(context);
		Machine.createMachine(player, input, placedLocation);
		Common.tell(player, "&eCommercial Machine successfully installed.");
		SFXManager.playWorldSound(placedLocation, Sound.BLOCK_BEACON_POWER_SELECT, 0.8F, 1.4F);
		return Prompt.END_OF_CONVERSATION;
	}
}