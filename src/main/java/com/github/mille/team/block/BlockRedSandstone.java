package com.github.mille.team.block;

import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemBlock;
import com.github.mille.team.item.ItemTool;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockRedSandstone extends BlockSandstone {

    public BlockRedSandstone() {
        this(0);
    }

    public BlockRedSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RED_SANDSTONE;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
            "Red Sandstone",
            "Chiseled Red Sandstone",
            "Smooth Red Sandstone",
            ""
        };

        return names[this.meta & 0x03];
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
    public Item toItem() {
        return new ItemBlock(this, this.meta & 0x03);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
