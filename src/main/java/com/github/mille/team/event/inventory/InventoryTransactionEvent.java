package com.github.mille.team.event.inventory;

import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.Event;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.inventory.TransactionGroup;

/**
 * author: MagicDroidX Nukkit Project
 */
public class InventoryTransactionEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final TransactionGroup transaction;

    public InventoryTransactionEvent(TransactionGroup transaction) {
        this.transaction = transaction;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public TransactionGroup getTransaction() {
        return transaction;
    }

}