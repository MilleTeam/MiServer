package com.github.mille.team.block;

import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemQuartz;
import com.github.mille.team.item.ItemTool;
import com.github.mille.team.math.NukkitRandom;

/**
 * Created on 2015/12/26 by xtypr. Package com.github.mille.team.block in project Nukkit .
 */
public class BlockOreQuartz extends BlockSolid {

    public BlockOreQuartz() {
        this(0);
    }

    public BlockOreQuartz(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Quartz Ore";
    }

    @Override
    public int getId() {
        return QUARTZ_ORE;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                new ItemQuartz()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public int getDropExp() {
        return new NukkitRandom().nextRange(1, 5);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
