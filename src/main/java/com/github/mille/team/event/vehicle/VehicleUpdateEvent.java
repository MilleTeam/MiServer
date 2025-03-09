package com.github.mille.team.event.vehicle;

import com.github.mille.team.entity.item.EntityVehicle;
import com.github.mille.team.event.HandlerList;

public class VehicleUpdateEvent extends VehicleEvent {

    private static final HandlerList handlers = new HandlerList();

    public VehicleUpdateEvent(EntityVehicle vehicle) {
        super(vehicle);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
