package com.github.mille.team.event.entity;

import com.github.mille.team.entity.Entity;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

public class EntityPortalEnterEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final PortalType type;

    public EntityPortalEnterEvent(
        Entity entity,
        PortalType type
    ) {
        this.entity = entity;
        this.type = type;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PortalType getPortalType() {
        return type;
    }

    public enum PortalType {
        NETHER,
        END
    }

}
