package com.github.mille.team.event.entity;

import com.github.mille.team.entity.item.EntityItem;
import com.github.mille.team.event.HandlerList;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ItemSpawnEvent extends EntityEvent {

    private static final HandlerList handlers = new HandlerList();

    public ItemSpawnEvent(EntityItem item) {
        this.entity = item;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public EntityItem getEntity() {
        return (EntityItem) this.entity;
    }

}
