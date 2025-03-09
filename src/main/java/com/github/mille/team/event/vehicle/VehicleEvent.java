package com.github.mille.team.event.vehicle;

import com.github.mille.team.entity.item.EntityVehicle;
import com.github.mille.team.event.Event;

/**
 * Created by larryTheCoder at 7/5/2017.
 * <p>
 * Nukkit Project
 */
public abstract class VehicleEvent extends Event {

    private final EntityVehicle vehicle;

    public VehicleEvent(EntityVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public EntityVehicle getVehicle() {
        return vehicle;
    }

}
