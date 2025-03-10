package com.github.mille.team.block;

import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemMelon;
import com.github.mille.team.item.ItemTool;
import com.github.mille.team.utils.BlockColor;

import java.util.Random;

/**
 * Created on 2015/12/11 by Pub4Game. Package com.github.mille.team.block in project Nukkit .
 */

public class BlockMelon extends BlockSolid {

    public BlockMelon() {
        this(0);
    }

    public BlockMelon(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return MELON_BLOCK;
    }

    public String getName() {
        return "Melon Block";
    }

    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
            new ItemMelon(0, new Random().nextInt(4) + 3)
        };
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

}
