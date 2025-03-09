package com.github.mille.team.event.block;

import com.github.mille.team.block.Block;
import com.github.mille.team.entity.Entity;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

public class BlockIgniteEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Block source;

    private final Entity entity;

    private final BlockIgniteCause cause;

    public BlockIgniteEvent(
        Block block,
        Block source,
        Entity entity,
        BlockIgniteCause cause
    ) {
        super(block);
        this.source = source;
        this.entity = entity;
        this.cause = cause;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Block getSource() {
        return source;
    }

    public Entity getEntity() {
        return entity;
    }

    public BlockIgniteCause getCause() {
        return cause;
    }

    public enum BlockIgniteCause {
        EXPLOSION,
        FIREBALL,
        FLINT_AND_STEEL,
        LAVA,
        LIGHTNING,
        SPREAD
    }

}
