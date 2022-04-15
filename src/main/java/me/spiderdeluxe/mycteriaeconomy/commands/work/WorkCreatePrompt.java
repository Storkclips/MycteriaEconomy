package me.spiderdeluxe.mycteriaeconomy.commands.work;

import me.spiderdeluxe.mycteriaeconomy.models.work.BaseWork;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.conversation.SimplePrompt;

public class WorkCreatePrompt extends SimplePrompt {


	BaseWork work;

	public WorkCreatePrompt(final BaseWork work) {
		super(false);
		this.work = work;
	}

	@Override
	protected String getPrompt(final ConversationContext ctx) {
		return "&6Enter the name of the work in chat.";
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input) {
		return input.length() >= 3 && input.length() <= 30;
	}

	@Override
	protected String getFailedValidationText(final ConversationContext context, final String invalidInput) {
		return "Name must be greater than 3 letters and less than 30";
	}

	@Override
	protected @Nullable
	Prompt acceptValidatedInput(@NotNull final ConversationContext context, @NotNull final String input) {
		final Player player = getPlayer(context);
		tell(player, "You have successfully created your own work!");
		work.setName(input);

		return Prompt.END_OF_CONVERSATION;
	}
}
