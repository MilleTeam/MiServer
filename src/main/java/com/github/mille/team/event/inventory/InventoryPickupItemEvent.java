package com.github.mille.team.event.inventory;

import com.github.mille.team.entity.item.EntityItem;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.inventory.Inventory;

/**
 * author: MagicDroidX Nukkit Project
 */
public class InventoryPickupItemEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final EntityItem item;

    public InventoryPickupItemEvent(
        Inventory inventory,
        EntityItem item
    ) {
        super(inventory);
        this.item = item;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EntityItem getItem() {
        return item;
    }

}