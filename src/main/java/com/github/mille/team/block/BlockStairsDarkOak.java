package com.github.mille.team.block;

/**
 * Created on 2015/11/25 by xtypr. Package com.github.mille.team.block in project Nukkit .
 */
public class BlockStairsDarkOak extends BlockStairsWood {

    public BlockStairsDarkOak() {
        this(0);
    }

    public BlockStairsDarkOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DARK_OAK_WOOD_STAIRS;
    }

    @Override
    public String getName() {
        return "Dark Oak Wood Stairs";
    }

}
