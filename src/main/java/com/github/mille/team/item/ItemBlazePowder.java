package com.github.mille.team.item;

public class ItemBlazePowder extends Item {

    public ItemBlazePowder() {
        this(0, 1);
    }

    public ItemBlazePowder(Integer meta) {
        this(meta, 1);
    }

    public ItemBlazePowder(
        Integer meta,
        int count
    ) {
        super(BLAZE_POWDER, meta, count, "Blaze Powder");
    }

}
