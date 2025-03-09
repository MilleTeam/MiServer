package com.github.mille.team.item;

import com.github.mille.team.block.BlockHopper;

/**
 * Created by CreeperFace on 13.5.2017.
 */
public class ItemHopper extends Item {

    public ItemHopper() {
        this(0);
    }

    public ItemHopper(Integer meta) {
        this(meta, 1);
    }

    public ItemHopper(
        Integer meta,
        int count
    ) {
        super(HOPPER, 0, count, "Hopper");
        this.block = new BlockHopper();
    }

}
