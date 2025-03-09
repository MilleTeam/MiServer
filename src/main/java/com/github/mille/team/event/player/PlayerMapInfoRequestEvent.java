package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.item.Item;

/**
 * Created by CreeperFace on 18.3.2017.
 */
public class PlayerMapInfoRequestEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Item item;

    public PlayerMapInfoRequestEvent(
        Player p,
        Item item
    ) {
        this.player = p;
        this.item = item;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Item getMap() {
        return item;
    }

}
