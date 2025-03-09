package com.github.mille.team.event.block;

import com.github.mille.team.block.Block;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

/**
 * author: MagicDroidX Nukkit Project
 */
public class BlockGrowEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Block newState;

    public BlockGrowEvent(
        Block block,
        Block newState
    ) {
        super(block);
        this.newState = newState;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Block getNewState() {
        return newState;
    }

}
