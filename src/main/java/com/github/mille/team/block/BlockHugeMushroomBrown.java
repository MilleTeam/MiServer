package com.github.mille.team.block;

import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemBlock;
import com.github.mille.team.item.ItemTool;
import com.github.mille.team.math.NukkitRandom;

/**
 * Created by Pub4Game on 28.01.2016.
 */
public class BlockHugeMushroomBrown extends BlockSolid {

    public BlockHugeMushroomBrown() {
        this(0);
    }

    public BlockHugeMushroomBrown(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Brown Mushroom Block";
    }

    @Override
    public int getId() {
        return BROWN_MUSHROOM_BLOCK;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (new NukkitRandom().nextRange(1, 20) == 0) {
            return new Item[]{
                new ItemBlock(new BlockMushroomBrown())
            };
        } else {
            return new Item[0];
        }
    }

}
