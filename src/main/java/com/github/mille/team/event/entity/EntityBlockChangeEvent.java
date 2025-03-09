package com.github.mille.team.event.entity;

import com.github.mille.team.block.Block;
import com.github.mille.team.entity.Entity;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

/**
 * Created on 15-10-26.
 */
public class EntityBlockChangeEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Block from;

    private final Block to;

    public EntityBlockChangeEvent(
        Entity entity,
        Block from,
        Block to
    ) {
        this.entity = entity;
        this.from = from;
        this.to = to;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Block getFrom() {
        return from;
    }

    public Block getTo() {
        return to;
    }

}
