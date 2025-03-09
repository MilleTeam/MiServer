package com.github.mille.team.item;

/**
 * Created by Snake1999 on 2016/1/14. Package com.github.mille.team.item in project nukkit.
 */
public class ItemSalmon extends ItemFish {

    public ItemSalmon() {
        this(0, 1);
    }

    public ItemSalmon(Integer meta) {
        this(meta, 1);
    }

    public ItemSalmon(
        Integer meta,
        int count
    ) {
        super(RAW_SALMON, meta, count, "Raw Salmon");
    }

}
