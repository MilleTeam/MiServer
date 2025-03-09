package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

/**
 * call when a player moves wrongly
 *
 * @author WilliamGao
 */

public class PlayerInvalidMoveEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean revert;

    public PlayerInvalidMoveEvent(
        Player player,
        boolean revert
    ) {
        this.player = player;
        this.revert = revert;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public boolean isRevert() {
        return this.revert;
    }

    /**
     * @deprecated If you just simply want to disable the movement check, please use {@link Player#setCheckMovement(boolean)} instead.
     */
    @Deprecated
    public void setRevert(boolean revert) {
        this.revert = revert;
    }

}
