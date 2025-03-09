package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.block.Block;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.item.Item;
import com.github.mille.team.math.BlockFace;

public class PlayerBucketEmptyEvent extends PlayerBucketEvent {

    private static final HandlerList handlers = new HandlerList();

    public PlayerBucketEmptyEvent(
        Player who,
        Block blockClicked,
        BlockFace blockFace,
        Item bucket,
        Item itemInHand
    ) {
        super(who, blockClicked, blockFace, bucket, itemInHand);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
