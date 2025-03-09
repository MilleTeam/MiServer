package com.github.mille.team.block;

import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemDye;
import com.github.mille.team.item.ItemTool;
import com.github.mille.team.math.NukkitRandom;

import java.util.Random;

/**
 * author: MagicDroidX Nukkit Project
 */
public class BlockOreLapis extends BlockSolid {


    public BlockOreLapis() {
        this(0);
    }

    public BlockOreLapis(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return LAPIS_ORE;
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
    public String getName() {
        return "Lapis Lazuli Ore";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_STONE) {
            return new Item[]{
                new ItemDye(4, new Random().nextInt(4) + 4)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public int getDropExp() {
        return new NukkitRandom().nextRange(2, 5);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
