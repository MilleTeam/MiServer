package com.github.mille.team.item;

import com.github.mille.team.block.BlockRedstoneComparatorUnpowered;

/**
 * @author CreeperFace
 */
public class ItemRedstoneComparator extends Item {

    public ItemRedstoneComparator() {
        this(0);
    }

    public ItemRedstoneComparator(Integer meta) {
        this(0, 1);
    }

    public ItemRedstoneComparator(
        Integer meta,
        int count
    ) {
        super(COMPARATOR, meta, count, "Redstone Comparator");
        this.block = new BlockRedstoneComparatorUnpowered();
    }

}