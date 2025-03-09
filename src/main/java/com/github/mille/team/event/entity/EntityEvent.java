package com.github.mille.team.event.entity;

import com.github.mille.team.entity.Entity;
import com.github.mille.team.event.Event;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class EntityEvent extends Event {

    protected Entity entity;

    public Entity getEntity() {
        return entity;
    }

}
