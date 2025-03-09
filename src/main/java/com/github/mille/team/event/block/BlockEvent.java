package com.github.mille.team.event.block;

import com.github.mille.team.block.Block;
import com.github.mille.team.event.Event;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class BlockEvent extends Event {

    protected final Block block;

    public BlockEvent(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

}
