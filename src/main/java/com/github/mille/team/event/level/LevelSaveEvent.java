package com.github.mille.team.event.level;

import com.github.mille.team.event.HandlerList;
import com.github.mille.team.level.Level;

/**
 * author: MagicDroidX Nukkit Project
 */
public class LevelSaveEvent extends LevelEvent {

    private static final HandlerList handlers = new HandlerList();

    public LevelSaveEvent(Level level) {
        super(level);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
