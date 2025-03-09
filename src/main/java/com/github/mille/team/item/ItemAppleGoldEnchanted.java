package com.github.mille.team.item;

/**
 * Created by Snake1999 on 2016/1/14. Package com.github.mille.team.item in project nukkit.
 */
public class ItemAppleGoldEnchanted extends ItemEdible {

    public ItemAppleGoldEnchanted() {
        this(0, 1);
    }

    public ItemAppleGoldEnchanted(Integer meta) {
        this(meta, 1);
    }

    public ItemAppleGoldEnchanted(
        Integer meta,
        int count
    ) {
        super(GOLDEN_APPLE_ENCHANTED, meta, count, "Enchanted Golden Apple");
    }

}
