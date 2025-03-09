package com.github.mille.team.event.block;

import com.github.mille.team.Player;
import com.github.mille.team.block.Block;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.item.Item;

/**
 * author: MagicDroidX Nukkit Project
 */
public class BlockBreakEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    protected final Player player;

    protected final Item item;

    protected boolean instaBreak = false;

    protected Item[] blockDrops = new Item[0];

    protected boolean fastBreak = false;

    public BlockBreakEvent(
        Player player,
        Block block,
        Item item
    ) {
        this(player, block, item, false, false);
    }

    public BlockBreakEvent(
        Player player,
        Block block,
        Item item,
        boolean instaBreak
    ) {
        this(player, block, item, instaBreak, false);
    }

    public BlockBreakEvent(
        Player player,
        Block block,
        Item item,
        boolean instaBreak,
        boolean fastBreak
    ) {
        super(block);
        this.item = item;
        this.player = player;
        this.instaBreak = instaBreak;
        this.blockDrops = player.isSurvival() ? block.getDrops(item) : new Item[0];
        this.fastBreak = fastBreak;
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

    public boolean getInstaBreak() {
        return this.instaBreak;
    }

    public void setInstaBreak(boolean instaBreak) {
        this.instaBreak = instaBreak;
    }

    public Item[] getDrops() {
        return blockDrops;
    }

    public void setDrops(Item[] drops) {
        this.blockDrops = drops;
    }

    public boolean isFastBreak() {
        return this.fastBreak;
    }

}
