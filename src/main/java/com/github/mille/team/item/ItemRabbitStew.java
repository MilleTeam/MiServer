package com.github.mille.team.item;

/**
 * Created by Snake1999 on 2016/1/14. Package com.github.mille.team.item in project nukkit.
 */
public class ItemRabbitStew extends ItemEdible {

    public ItemRabbitStew() {
        this(0, 1);
    }

    public ItemRabbitStew(Integer meta) {
        this(meta, 1);
    }

    public ItemRabbitStew(
        Integer meta,
        int count
    ) {
        super(RABBIT_STEW, meta, count, "Rabbit Stew");
    }

}
