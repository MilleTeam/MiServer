package com.github.mille.team.block;

import com.github.mille.team.item.ItemTool;
import com.github.mille.team.utils.BlockColor;

/**
 * Created on 2015/11/25 by xtypr. Package com.github.mille.team.block in project Nukkit .
 */
public class BlockStairsCobblestone extends BlockStairs {

    public BlockStairsCobblestone() {
        this(0);
    }

    public BlockStairsCobblestone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COBBLESTONE_STAIRS;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Cobblestone Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
