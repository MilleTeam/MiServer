package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.block.Block;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.item.Item;
import com.github.mille.team.math.BlockFace;

abstract class PlayerBucketEvent extends PlayerEvent implements Cancellable {

    private final Block blockClicked;

    private final BlockFace blockFace;

    private final Item bucket;

    private Item item;


    public PlayerBucketEvent(
        Player who,
        Block blockClicked,
        BlockFace blockFace,
        Item bucket,
        Item itemInHand
    ) {
        this.player = who;
        this.blockClicked = blockClicked;
        this.blockFace = blockFace;
        this.item = itemInHand;
        this.bucket = bucket;
    }

    /**
     * Returns the bucket used in this event
     */
    public Item getBucket() {
        return this.bucket;
    }

    /**
     * Returns the item in hand after the event
     */
    public Item getItem() {
        return this.item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Block getBlockClicked() {
        return this.blockClicked;
    }

    public BlockFace getBlockFace() {
        return this.blockFace;
    }

}
