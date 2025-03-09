package com.github.mille.team.event.entity;

import com.github.mille.team.entity.Entity;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.math.Vector3;

/**
 * author: MagicDroidX Nukkit Project
 */
public class EntityMotionEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Vector3 motion;

    public EntityMotionEvent(
        Entity entity,
        Vector3 motion
    ) {
        this.entity = entity;
        this.motion = motion;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Deprecated
    public Vector3 getVector() {
        return this.motion;
    }

    public Vector3 getMotion() {
        return this.motion;
    }

}
