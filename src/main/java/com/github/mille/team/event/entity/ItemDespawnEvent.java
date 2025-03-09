package com.github.mille.team.event.entity;

import com.github.mille.team.entity.item.EntityItem;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ItemDespawnEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public ItemDespawnEvent(EntityItem item) {
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
