package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

public class PlayerToggleSneakEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    protected final boolean isSneaking;

    public PlayerToggleSneakEvent(
        Player player,
        boolean isSneaking
    ) {
        this.player = player;
        this.isSneaking = isSneaking;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public boolean isSneaking() {
        return this.isSneaking;
    }

}
