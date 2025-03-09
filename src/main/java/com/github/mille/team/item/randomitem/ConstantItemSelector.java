package com.github.mille.team.item.randomitem;

import com.github.mille.team.item.Item;

/**
 * Created by Snake1999 on 2016/1/15. Package com.github.mille.team.item.randomitem in project nukkit.
 */
public class ConstantItemSelector extends Selector {

    protected final Item item;

    public ConstantItemSelector(
        int id,
        Selector parent
    ) {
        this(id, 0, parent);
    }

    public ConstantItemSelector(
        int id,
        Integer meta,
        Selector parent
    ) {
        this(id, meta, 1, parent);
    }

    public ConstantItemSelector(
        int id,
        Integer meta,
        int count,
        Selector parent
    ) {
        this(new Item(id, meta, count), parent);
    }

    public ConstantItemSelector(
        Item item,
        Selector parent
    ) {
        super(parent);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public Object select() {
        return getItem();
    }

}
