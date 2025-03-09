package com.github.mille.team.item;

/**
 * Created by Snake1999 on 2016/1/14. Package com.github.mille.team.item in project nukkit.
 */
public class ItemRabbitCooked extends ItemEdible {

    public ItemRabbitCooked() {
        this(0, 1);
    }

    public ItemRabbitCooked(Integer meta) {
        this(meta, 1);
    }

    public ItemRabbitCooked(
        Integer meta,
        int count
    ) {
        super(COOKED_RABBIT, meta, count, "Cooked Rabbit");
    }

}
