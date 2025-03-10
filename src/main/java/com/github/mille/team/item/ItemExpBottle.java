package com.github.mille.team.item;

/**
 * Created on 2015/12/25 by xtypr. Package com.github.mille.team.item in project Nukkit .
 */
public class ItemExpBottle extends Item {

    public ItemExpBottle() {
        this(0, 1);
    }

    public ItemExpBottle(Integer meta) {
        this(meta, 1);
    }

    public ItemExpBottle(
        Integer meta,
        int count
    ) {
        super(EXPERIENCE_BOTTLE, meta, count, "Bottle o' Enchanting");
    }

}
