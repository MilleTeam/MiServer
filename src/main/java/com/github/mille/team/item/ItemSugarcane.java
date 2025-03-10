package com.github.mille.team.item;

import com.github.mille.team.block.BlockSugarcane;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ItemSugarcane extends Item {

    public ItemSugarcane() {
        this(0, 1);
    }

    public ItemSugarcane(Integer meta) {
        this(meta, 1);
    }

    public ItemSugarcane(
        Integer meta,
        int count
    ) {
        super(SUGARCANE, 0, count, "Sugar Cane");
        this.block = new BlockSugarcane();
    }

}
