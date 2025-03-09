package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

public class PlayerCommandPreprocessEvent extends PlayerMessageEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public PlayerCommandPreprocessEvent(
        Player player,
        String message
    ) {
        this.player = player;
        this.message = message;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}
