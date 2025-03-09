package com.github.mille.team.event.level;

import com.github.mille.team.event.Event;
import com.github.mille.team.level.Level;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class LevelEvent extends Event {

    private final Level level;

    public LevelEvent(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

}
