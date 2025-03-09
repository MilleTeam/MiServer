package com.github.mille.team.event.entity;

import com.github.mille.team.entity.Entity;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.level.Location;

/**
 * author: MagicDroidX Nukkit Project
 */
public class EntityTeleportEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Location from;

    private Location to;

    public EntityTeleportEvent(
        Entity entity,
        Location from,
        Location to
    ) {
        this.entity = entity;
        this.from = from;
        this.to = to;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
    }

}
