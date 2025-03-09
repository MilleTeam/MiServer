package com.github.mille.team.event.entity;

import com.github.mille.team.entity.Entity;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.level.Level;

/**
 * author: MagicDroidX Nukkit Project
 */
public class EntityLevelChangeEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Level originLevel;

    private final Level targetLevel;

    public EntityLevelChangeEvent(
        Entity entity,
        Level originLevel,
        Level targetLevel
    ) {
        this.entity = entity;
        this.originLevel = originLevel;
        this.targetLevel = targetLevel;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Level getOrigin() {
        return originLevel;
    }

    public Level getTarget() {
        return targetLevel;
    }

}
