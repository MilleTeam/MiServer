package com.github.mille.team.event.entity;

import com.github.mille.team.entity.projectile.EntityProjectile;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

public class ProjectileLaunchEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public ProjectileLaunchEvent(EntityProjectile entity) {
        this.entity = entity;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EntityProjectile getEntity() {
        return (EntityProjectile) this.entity;
    }

}
