package com.github.mille.team.event.block;

import com.github.mille.team.block.Block;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

public class BlockFromToEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Block to;

    public BlockFromToEvent(
        Block block,
        Block to
    ) {
        super(block);
        this.to = to;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Block getTo() {
        return to;
    }

}