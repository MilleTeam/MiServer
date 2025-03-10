package com.github.mille.team.item;

import com.github.mille.team.block.BlockBed;
import com.github.mille.team.utils.DyeColor;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ItemBed extends Item {

    public ItemBed() {
        this(0, 1);
    }

    public ItemBed(Integer meta) {
        this(meta, 1);
    }

    public ItemBed(
        Integer meta,
        int count
    ) {
        super(BED, meta, count, DyeColor.getByWoolData(meta).getName() + " Bed");
        this.block = new BlockBed();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

}
