package com.github.mille.team.block;

import com.github.mille.team.item.Item;

/**
 * author: Angelic47 Nukkit Project
 */
public class BlockGlass extends BlockTransparent {

    public BlockGlass() {
        this(0);
    }

    public BlockGlass(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GLASS;
    }

    @Override
    public String getName() {
        return "Glass";
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

}
