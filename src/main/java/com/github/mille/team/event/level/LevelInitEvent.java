package com.github.mille.team.event.level;

import com.github.mille.team.event.HandlerList;
import com.github.mille.team.level.Level;

/**
 * author: MagicDroidX Nukkit Project
 */
public class LevelInitEvent extends LevelEvent {

    private static final HandlerList handlers = new HandlerList();

    public LevelInitEvent(Level level) {
        super(level);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
