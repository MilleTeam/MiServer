package com.github.mille.team.command.defaults;

import com.github.mille.team.IPlayer;
import com.github.mille.team.Player;
import com.github.mille.team.command.Command;
import com.github.mille.team.command.CommandSender;
import com.github.mille.team.command.data.CommandParameter;
import com.github.mille.team.lang.TranslationContainer;
import com.github.mille.team.utils.TextFormat;

/**
 * Created on 2015/11/12 by xtypr. Package com.github.mille.team.command.defaults in project Nukkit .
 */
public class DeopCommand extends VanillaCommand {

    public DeopCommand(String name) {
        super(name, "%nukkit.command.deop.description", "%commands.deop.usage");
        this.setPermission("nukkit.command.op.take");
        this.commandParameters.put("default", new CommandParameter[]{
            new CommandParameter("player", CommandParameter.ARG_TYPE_TARGET, false)
        });
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

        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return false;
        }

        String playerName = args[0];
        IPlayer player = sender.getServer().getOfflinePlayer(playerName);
        player.setOp(false);

        if (player instanceof Player) {
            ((Player) player).sendMessage(TextFormat.GRAY + "You are no longer op!");
        }

        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.deop.success", new String[]{player.getName()}));

        return true;
    }

}
