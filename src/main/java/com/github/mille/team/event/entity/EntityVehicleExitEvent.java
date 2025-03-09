package com.github.mille.team.event.entity;

import com.github.mille.team.entity.Entity;
import com.github.mille.team.entity.item.EntityVehicle;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

public class EntityVehicleExitEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final EntityVehicle vehicle;

    public EntityVehicleExitEvent(
        Entity entity,
        EntityVehicle vehicle
    ) {
        this.entity = entity;
        this.vehicle = vehicle;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EntityVehicle getVehicle() {
        return vehicle;
    }

}
