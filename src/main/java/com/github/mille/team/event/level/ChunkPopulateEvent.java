package com.github.mille.team.event.level;

import com.github.mille.team.event.HandlerList;
import com.github.mille.team.level.format.FullChunk;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ChunkPopulateEvent extends ChunkEvent {

    private static final HandlerList handlers = new HandlerList();

    public ChunkPopulateEvent(FullChunk chunk) {
        super(chunk);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}