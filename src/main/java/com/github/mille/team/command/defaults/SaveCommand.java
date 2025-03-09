package com.github.mille.team.command.defaults;

import com.github.mille.team.Player;
import com.github.mille.team.command.Command;
import com.github.mille.team.command.CommandSender;
import com.github.mille.team.lang.TranslationContainer;
import com.github.mille.team.level.Level;

/**
 * Created on 2015/11/13 by xtypr. Package com.github.mille.team.command.defaults in project Nukkit .
 */
public class SaveCommand extends VanillaCommand {

    public SaveCommand(String name) {
        super(name, "%nukkit.command.save.description", "%commands.save.usage");
        this.setPermission("nukkit.command.save.perform");
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

        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.save.start"));

        for (Player player : sender.getServer().getOnlinePlayers().values()) {
            player.save();
        }

        for (Level level : sender.getServer().getLevels().values()) {
            level.save(true);
        }

        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.save.success"));
        return true;
    }

}
