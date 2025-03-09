package com.github.mille.team.event.entity;

import com.github.mille.team.entity.Entity;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.item.Item;

/**
 * author: MagicDroidX Nukkit Project
 */
public class EntityArmorChangeEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Item oldItem;

    private final int slot;

    private Item newItem;

    public EntityArmorChangeEvent(
        Entity entity,
        Item oldItem,
        Item newItem,
        int slot
    ) {
        this.entity = entity;
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.slot = slot;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public int getSlot() {
        return slot;
    }

    public Item getNewItem() {
        return newItem;
    }

    public void setNewItem(Item newItem) {
        this.newItem = newItem;
    }

    public Item getOldItem() {
        return oldItem;
    }

}
