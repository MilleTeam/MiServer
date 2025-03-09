package com.github.mille.team.event.level;

import com.github.mille.team.level.format.FullChunk;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class ChunkEvent extends LevelEvent {

    private final FullChunk chunk;

    public ChunkEvent(FullChunk chunk) {
        super(chunk.getProvider().getLevel());
        this.chunk = chunk;
    }

    public FullChunk getChunk() {
        return chunk;
    }

}
