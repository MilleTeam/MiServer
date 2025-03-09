package com.github.mille.team.item;

import com.github.mille.team.block.BlockTripWire;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ItemString extends Item {

    public ItemString() {
        this(0, 1);
    }

    public ItemString(Integer meta) {
        this(meta, 1);
    }

    public ItemString(
        Integer meta,
        int count
    ) {
        super(STRING, meta, count, "String");
        this.block = new BlockTripWire();
    }

}
