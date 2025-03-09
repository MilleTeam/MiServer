package com.github.mille.team.command.defaults;

import com.github.mille.team.command.CommandSender;
import com.github.mille.team.lang.TranslationContainer;
import com.github.mille.team.plugin.Plugin;
import com.github.mille.team.utils.TextFormat;

import java.util.Map;

/**
 * Created on 2015/11/12 by xtypr. Package com.github.mille.team.command.defaults in project Nukkit .
 */
public class PluginsCommand extends VanillaCommand {

    public PluginsCommand(String name) {
        super(
            name,
            "%nukkit.command.plugins.description",
            "%nukkit.command.plugins.usage",
            new String[]{"pl"}
        );
        this.setPermission("nukkit.command.plugins");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(
        CommandSender sender,
        String commandLabel,
        String[] args
    ) {
        if (!this.testPermission(sender)) {
            return true;
        }

        this.sendPluginList(sender);
        return true;
    }

    private void sendPluginList(CommandSender sender) {
        String list = "";
        Map<String, Plugin> plugins = sender.getServer().getPluginManager().getPlugins();
        for (Plugin plugin : plugins.values()) {
            if (list.length() > 0) {
                list += TextFormat.WHITE + ", ";
            }
            list += plugin.isEnabled() ? TextFormat.GREEN : TextFormat.RED;
            list += plugin.getDescription().getFullName();
        }

        sender.sendMessage(new TranslationContainer("nukkit.command.plugins.success", new String[]{String.valueOf(plugins.size()), list}));
    }

}
