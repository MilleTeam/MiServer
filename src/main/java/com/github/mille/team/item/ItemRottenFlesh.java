package com.github.mille.team.item;

/**
 * Created by Snake1999 on 2016/1/14. Package com.github.mille.team.item in project nukkit.
 */
public class ItemRottenFlesh extends ItemEdible {

    public ItemRottenFlesh() {
        this(0, 1);
    }

    public ItemRottenFlesh(Integer meta) {
        this(meta, 1);
    }

    public ItemRottenFlesh(
        Integer meta,
        int count
    ) {
        super(ROTTEN_FLESH, meta, count, "Rotten Flesh");
    }

}
