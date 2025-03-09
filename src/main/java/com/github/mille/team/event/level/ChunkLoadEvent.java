package com.github.mille.team.event.level;

import com.github.mille.team.event.HandlerList;
import com.github.mille.team.level.format.FullChunk;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ChunkLoadEvent extends ChunkEvent {

    private static final HandlerList handlers = new HandlerList();

    private final boolean newChunk;

    public ChunkLoadEvent(
        FullChunk chunk,
        boolean newChunk
    ) {
        super(chunk);
        this.newChunk = newChunk;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public boolean isNewChunk() {
        return newChunk;
    }

}