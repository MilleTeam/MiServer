package com.github.mille.team.event.entity;

import com.github.mille.team.entity.Entity;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

/**
 * author: MagicDroidX Nukkit Project
 */
public class EntityCombustEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    protected int duration;

    public EntityCombustEvent(
        Entity combustee,
        int duration
    ) {
        this.entity = combustee;
        this.duration = duration;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}
