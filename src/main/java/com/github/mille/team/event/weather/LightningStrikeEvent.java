package com.github.mille.team.event.weather;

import com.github.mille.team.entity.weather.EntityLightningStrike;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.level.Level;

/**
 * author: funcraft Nukkit Project
 */
public class LightningStrikeEvent extends WeatherEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final EntityLightningStrike bolt;

    public LightningStrikeEvent(
        Level level,
        final EntityLightningStrike bolt
    ) {
        super(level);
        this.bolt = bolt;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    /**
     * * Gets the bolt which is striking the earth. * @return lightning entity
     */
    public EntityLightningStrike getLightning() {
        return bolt;
    }

}
