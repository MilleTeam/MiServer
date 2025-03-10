package com.github.mille.team.block;

import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemTool;

/**
 * Created on 2015/11/22 by xtypr. Package com.github.mille.team.block in project Nukkit .
 */
public class BlockObsidianGlowing extends BlockSolid {

    public BlockObsidianGlowing() {
        this(0);
    }

    public BlockObsidianGlowing(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GLOWING_OBSIDIAN;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Glowing Obsidian";
    }

    @Override
    public double getHardness() {
        return 50;
    }

    @Override
    public double getResistance() {
        return 6000;
    }

    @Override
    public int getLightLevel() {
        return 12;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() > ItemTool.DIAMOND_PICKAXE) {
            return new Item[]{
                toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
