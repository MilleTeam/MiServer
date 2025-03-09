package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

public class PlayerToggleGlideEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    protected final boolean isGliding;

    public PlayerToggleGlideEvent(
        Player player,
        boolean isSneaking
    ) {
        this.player = player;
        this.isGliding = isSneaking;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public boolean isGliding() {
        return this.isGliding;
    }

}
