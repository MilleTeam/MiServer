package com.github.mille.team.block;

import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemTool;
import com.github.mille.team.utils.BlockColor;

/**
 * Created on 2015/12/26 by Pub4Game. Package com.github.mille.team.block in project Nukkit .
 */
public class BlockNetherrack extends BlockSolid {

    public BlockNetherrack() {
        this(0);
    }

    public BlockNetherrack(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return NETHERRACK;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Netherrack";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
