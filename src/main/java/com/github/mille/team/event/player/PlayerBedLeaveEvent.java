package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.block.Block;
import com.github.mille.team.event.HandlerList;

public class PlayerBedLeaveEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Block bed;

    public PlayerBedLeaveEvent(
        Player player,
        Block bed
    ) {
        this.player = player;
        this.bed = bed;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Block getBed() {
        return bed;
    }

}
