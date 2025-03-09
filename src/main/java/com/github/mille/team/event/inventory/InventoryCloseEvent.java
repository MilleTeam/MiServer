package com.github.mille.team.event.inventory;

import com.github.mille.team.Player;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.inventory.Inventory;

/**
 * author: Box Nukkit Project
 */
public class InventoryCloseEvent extends InventoryEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Player who;

    public InventoryCloseEvent(
        Inventory inventory,
        Player who
    ) {
        super(inventory);
        this.who = who;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.who;
    }

}
