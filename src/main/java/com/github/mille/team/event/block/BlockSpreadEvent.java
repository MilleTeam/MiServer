package com.github.mille.team.event.block;

import com.github.mille.team.block.Block;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

/**
 * author: MagicDroidX Nukkit Project
 */
public class BlockSpreadEvent extends BlockFormEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Block source;

    public BlockSpreadEvent(
        Block block,
        Block source,
        Block newState
    ) {
        super(block, newState);
        this.source = source;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Block getSource() {
        return source;
    }

}
