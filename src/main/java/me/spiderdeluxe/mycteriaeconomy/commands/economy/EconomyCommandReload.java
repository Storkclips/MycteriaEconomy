package me.spiderdeluxe.mycteriaeconomy.commands.economy;

import me.spiderdeluxe.mycteriaeconomy.settings.Settings;
import me.spiderdeluxe.mycteriaeconomy.util.Messager;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

/**
 * @author SpiderDeluxe
 * This command is used to reload the plugin's config.
 */
public class EconomyCommandReload extends SimpleSubCommand {

    public EconomyCommandReload(final SimpleCommandGroup parent) {
        super(parent, "reload");

        setDescription("Reloads the plugin's config.");
        setPermission("mycteriaeconomy.reload");
    }

    @Override
    public void onCommand() {
        Settings.resetSettingsCall();
        Messager.sendSuccessMessage(sender, "&aConfig reloaded.");
    }
}
