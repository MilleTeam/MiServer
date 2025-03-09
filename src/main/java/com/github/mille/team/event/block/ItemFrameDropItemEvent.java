package com.github.mille.team.event.block;

import com.github.mille.team.Player;
import com.github.mille.team.block.Block;
import com.github.mille.team.block.entity.BlockEntityItemFrame;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.item.Item;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class ItemFrameDropItemEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final Item item;

    private final BlockEntityItemFrame itemFrame;

    public ItemFrameDropItemEvent(
        Player player,
        Block block,
        BlockEntityItemFrame itemFrame,
        Item item
    ) {
        super(block);
        this.player = player;
        this.itemFrame = itemFrame;
        this.item = item;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public BlockEntityItemFrame getItemFrame() {
        return itemFrame;
    }

    public Item getItem() {
        return item;
    }

}