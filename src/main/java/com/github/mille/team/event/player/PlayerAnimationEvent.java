package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

public class PlayerAnimationEvent extends PlayerEvent implements Cancellable {

    public static final int ARM_SWING = 1;

    private static final HandlerList handlers = new HandlerList();

    private final int animationType;

    public PlayerAnimationEvent(Player player) {
        this(player, ARM_SWING);
    }

    public PlayerAnimationEvent(
        Player player,
        int animation
    ) {
        this.player = player;
        this.animationType = animation;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public int getAnimationType() {
        return this.animationType;
    }

}
