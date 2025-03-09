package com.github.mille.team.event.block;

import com.github.mille.team.Player;
import com.github.mille.team.block.Block;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.item.Item;

/**
 * author: MagicDroidX Nukkit Project
 */
public class BlockPlaceEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    protected final Player player;

    protected final Item item;

    protected final Block blockReplace;

    protected final Block blockAgainst;

    public BlockPlaceEvent(
        Player player,
        Block blockPlace,
        Block blockReplace,
        Block blockAgainst,
        Item item
    ) {
        super(blockPlace);
        this.blockReplace = blockReplace;
        this.blockAgainst = blockAgainst;
        this.item = item;
        this.player = player;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Item getItem() {
        return item;
    }

    public Block getBlockReplace() {
        return blockReplace;
    }

    public Block getBlockAgainst() {
        return blockAgainst;
    }

}
