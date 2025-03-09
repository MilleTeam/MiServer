package com.github.mille.team.event.block;

import com.github.mille.team.block.Block;
import com.github.mille.team.event.HandlerList;

/**
 * Created by CreeperFace on 12.5.2017.
 */
public class BlockRedstoneEvent extends BlockEvent {

    private static final HandlerList handlers = new HandlerList();

    private final int oldPower;

    private final int newPower;

    public BlockRedstoneEvent(
        Block block,
        int oldPower,
        int newPower
    ) {
        super(block);
        this.oldPower = oldPower;
        this.newPower = newPower;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public int getOldPower() {
        return oldPower;
    }

    public int getNewPower() {
        return newPower;
    }

}
