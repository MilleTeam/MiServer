package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.item.Item;

/**
 * author: MagicDroidX Nukkit Project
 */
public class PlayerItemHeldEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Item item;

    private final int slot;

    private final int inventorySlot;

    public PlayerItemHeldEvent(
        Player player,
        Item item,
        int inventorySlot,
        int slot
    ) {
        this.player = player;
        this.item = item;
        this.inventorySlot = inventorySlot;
        this.slot = slot;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public int getSlot() {
        return slot;
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    public Item getItem() {
        return item;
    }

}
