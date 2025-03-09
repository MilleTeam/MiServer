package com.github.mille.team.event.vehicle;

import com.github.mille.team.entity.item.EntityVehicle;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.level.Location;

public class VehicleMoveEvent extends VehicleEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Location from;

    private final Location to;

    public VehicleMoveEvent(
        EntityVehicle vehicle,
        Location from,
        Location to
    ) {
        super(vehicle);
        this.from = from;
        this.to = to;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

}
