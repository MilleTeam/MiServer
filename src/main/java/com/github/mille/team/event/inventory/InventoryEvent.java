package com.github.mille.team.event.inventory;

import com.github.mille.team.Player;
import com.github.mille.team.event.Event;
import com.github.mille.team.inventory.Inventory;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class InventoryEvent extends Event {

    protected final Inventory inventory;

    public InventoryEvent(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Player[] getViewers() {
        return this.inventory.getViewers().stream().toArray(Player[]::new);
    }

}
