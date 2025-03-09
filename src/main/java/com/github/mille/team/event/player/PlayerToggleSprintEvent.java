package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

public class PlayerToggleSprintEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    protected final boolean isSprinting;

    public PlayerToggleSprintEvent(
        Player player,
        boolean isSprinting
    ) {
        this.player = player;
        this.isSprinting = isSprinting;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public boolean isSprinting() {
        return this.isSprinting;
    }

}
