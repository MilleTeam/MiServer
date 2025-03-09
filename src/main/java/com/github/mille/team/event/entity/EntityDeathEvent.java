package com.github.mille.team.event.entity;

import com.github.mille.team.entity.EntityLiving;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.item.Item;

/**
 * author: MagicDroidX Nukkit Project
 */
public class EntityDeathEvent extends EntityEvent {

    private static final HandlerList handlers = new HandlerList();

    private Item[] drops = new Item[0];

    public EntityDeathEvent(EntityLiving entity) {
        this(entity, new Item[0]);
    }

    public EntityDeathEvent(
        EntityLiving entity,
        Item[] drops
    ) {
        this.entity = entity;
        this.drops = drops;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Item[] getDrops() {
        return drops;
    }

    public void setDrops(Item[] drops) {
        this.drops = drops;
    }

}
