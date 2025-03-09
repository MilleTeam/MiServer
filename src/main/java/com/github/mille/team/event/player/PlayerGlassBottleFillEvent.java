package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.block.Block;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.item.Item;

public class PlayerGlassBottleFillEvent extends PlayerEvent implements Cancellable {

    protected final Item item;

    protected final Block target;

    public PlayerGlassBottleFillEvent(
        Player player,
        Block target,
        Item item
    ) {
        this.player = player;
        this.target = target;
        this.item = item.clone();
    }

    public Item getItem() {
        return item;
    }

    public Block getBlock() {
        return target;
    }

}
