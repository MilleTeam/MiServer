package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.item.Item;

/**
 * Called when a player eats something
 */
public class PlayerItemConsumeEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Item item;

    public PlayerItemConsumeEvent(
        Player player,
        Item item
    ) {
        this.player = player;
        this.item = item;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Item getItem() {
        return this.item.clone();
    }

}
