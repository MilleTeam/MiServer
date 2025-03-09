package com.github.mille.team.event.level;

import com.github.mille.team.event.HandlerList;
import com.github.mille.team.level.Level;
import com.github.mille.team.level.Position;

/**
 * author: MagicDroidX Nukkit Project
 */
public class SpawnChangeEvent extends LevelEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Position previousSpawn;

    public SpawnChangeEvent(
        Level level,
        Position previousSpawn
    ) {
        super(level);
        this.previousSpawn = previousSpawn;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Position getPreviousSpawn() {
        return previousSpawn;
    }

}
