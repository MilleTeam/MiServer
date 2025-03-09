package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.entity.Entity;
import com.github.mille.team.event.HandlerList;

public class PlayerMouseOverEntityEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Entity entity;

    public PlayerMouseOverEntityEvent(
        Player player,
        Entity entity
    ) {
        this.player = player;
        this.entity = entity;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Entity getEntity() {
        return entity;
    }

}
