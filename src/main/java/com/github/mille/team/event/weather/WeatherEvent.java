package com.github.mille.team.event.weather;

import com.github.mille.team.event.Event;
import com.github.mille.team.level.Level;

/**
 * author: funcraft Nukkit Project
 */
public abstract class WeatherEvent extends Event {

    private final Level level;

    public WeatherEvent(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

}
