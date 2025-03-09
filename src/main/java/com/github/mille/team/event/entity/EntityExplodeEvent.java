package com.github.mille.team.event.entity;

import com.github.mille.team.block.Block;
import com.github.mille.team.entity.Entity;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.level.Position;

import java.util.List;

/**
 * author: Angelic47 Nukkit Project
 */
public class EntityExplodeEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    protected final Position position;

    protected List<Block> blocks;

    protected double yield;

    public EntityExplodeEvent(
        Entity entity,
        Position position,
        List<Block> blocks,
        double yield
    ) {
        this.entity = entity;
        this.position = position;
        this.blocks = blocks;
        this.yield = yield;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Position getPosition() {
        return this.position;
    }

    public List<Block> getBlockList() {
        return this.blocks;
    }

    public void setBlockList(List<Block> blocks) {
        this.blocks = blocks;
    }

    public double getYield() {
        return this.yield;
    }

    public void setYield(double yield) {
        this.yield = yield;
    }

}
