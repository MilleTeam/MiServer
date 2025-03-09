package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

public class PlayerToggleFlightEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    protected final boolean isFlying;

    public PlayerToggleFlightEvent(
        Player player,
        boolean isFlying
    ) {
        this.player = player;
        this.isFlying = isFlying;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public boolean isFlying() {
        return this.isFlying;
    }

}
