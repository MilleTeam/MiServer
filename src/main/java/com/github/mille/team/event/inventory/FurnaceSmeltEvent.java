package com.github.mille.team.event.inventory;

import com.github.mille.team.block.entity.BlockEntityFurnace;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.event.block.BlockEvent;
import com.github.mille.team.item.Item;

/**
 * author: MagicDroidX Nukkit Project
 */
public class FurnaceSmeltEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final BlockEntityFurnace furnace;

    private final Item source;

    private Item result;

    public FurnaceSmeltEvent(
        BlockEntityFurnace furnace,
        Item source,
        Item result
    ) {
        super(furnace.getBlock());
        this.source = source.clone();
        this.source.setCount(1);
        this.result = result;
        this.furnace = furnace;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public BlockEntityFurnace getFurnace() {
        return furnace;
    }

    public Item getSource() {
        return source;
    }

    public Item getResult() {
        return result;
    }

    public void setResult(Item result) {
        this.result = result;
    }

}