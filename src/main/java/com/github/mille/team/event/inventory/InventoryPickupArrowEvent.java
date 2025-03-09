package com.github.mille.team.event.inventory;

import com.github.mille.team.entity.projectile.EntityArrow;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.inventory.Inventory;

/**
 * author: MagicDroidX Nukkit Project
 */
public class InventoryPickupArrowEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final EntityArrow arrow;

    public InventoryPickupArrowEvent(
        Inventory inventory,
        EntityArrow arrow
    ) {
        super(inventory);
        this.arrow = arrow;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EntityArrow getArrow() {
        return arrow;
    }

}