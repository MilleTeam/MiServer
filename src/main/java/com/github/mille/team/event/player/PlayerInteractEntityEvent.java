package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.entity.Entity;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.item.Item;

/**
 * Created by CreeperFace on 1. 1. 2017.
 */
public class PlayerInteractEntityEvent extends PlayerEvent implements Cancellable {


    private static final HandlerList handlers = new HandlerList();

    protected final Entity entity;

    protected final Item item;

    public PlayerInteractEntityEvent(
        Player player,
        Entity entity,
        Item item
    ) {
        this.player = player;
        this.entity = entity;
        this.item = item;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public Item getItem() {
        return this.item;
    }

}
