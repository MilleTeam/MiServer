package com.github.mille.team.event.block;

import com.github.mille.team.block.Block;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

/**
 * author: MagicDroidX Nukkit Project
 */
public class BlockFormEvent extends BlockGrowEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public BlockFormEvent(
        Block block,
        Block newState
    ) {
        super(block, newState);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
