package com.github.mille.team.event.entity;

import com.github.mille.team.block.Block;
import com.github.mille.team.entity.Entity;

/**
 * author: Box Nukkit Project
 */
public class EntityCombustByBlockEvent extends EntityCombustEvent {

    protected final Block combuster;

    public EntityCombustByBlockEvent(
        Block combuster,
        Entity combustee,
        int duration
    ) {
        super(combustee, duration);
        this.combuster = combuster;
    }

    public Block getCombuster() {
        return combuster;
    }

}
