package com.github.mille.team.event.server;

import com.github.mille.team.command.CommandSender;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ServerCommandEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    protected final CommandSender sender;

    protected String command;

    public ServerCommandEvent(
        CommandSender sender,
        String command
    ) {
        this.sender = sender;
        this.command = command;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

}
