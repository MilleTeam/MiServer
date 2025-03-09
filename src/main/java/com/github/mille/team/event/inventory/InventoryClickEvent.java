package com.github.mille.team.event.inventory;

import com.github.mille.team.Player;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.inventory.Inventory;
import com.github.mille.team.item.Item;

/**
 * author: boybook Nukkit Project
 */
public class InventoryClickEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final int slot;

    private final Item item;

    private final Player who;

    public InventoryClickEvent(
        Inventory inventory,
        Player who,
        int slot,
        Item item
    ) {
        super(inventory);
        this.slot = slot;
        this.who = who;
        this.item = item;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public int getSlot() {
        return slot;
    }

    public Item getItem() {
        return item;
    }

    public Player getWhoClicked() {
        return who;
    }

    public Player getPlayer() {
        return who;
    }

}