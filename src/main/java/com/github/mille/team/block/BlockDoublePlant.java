package com.github.mille.team.block;

import com.github.mille.team.Player;
import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemSeedsWheat;
import com.github.mille.team.level.Level;
import com.github.mille.team.math.BlockFace;
import com.github.mille.team.utils.BlockColor;

import java.util.Random;

/**
 * Created on 2015/11/23 by xtypr. Package com.github.mille.team.block in project Nukkit .
 */
public class BlockDoublePlant extends BlockFlowable {

    public BlockDoublePlant() {
        this(0);
    }

    public BlockDoublePlant(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_PLANT;
    }

    @Override
    public boolean canBeReplaced() {
        return this.meta == 2 || this.meta == 3;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
            "Sunflower",
            "Lilac",
            "Double Tallgrass",
            "Large Fern",
            "Rose Bush",
            "Peony"
        };
        return names[this.meta & 0x07];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if ((this.meta & 0x08) == 8) {
                // Top
                if (!(this.down().getId() == DOUBLE_PLANT)) {
                    this.getLevel().setBlock(this, new BlockAir(), true, true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            } else {
                // Bottom
                if (this.down().isTransparent() || !(this.up().getId() == DOUBLE_PLANT)) {
                    this.getLevel().useBreakOn(this);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean place(
        Item item,
        Block block,
        Block target,
        BlockFace face,
        double fx,
        double fy,
        double fz,
        Player player
    ) {
        Block down = down();
        Block up = up();

        if (up.getId() == 0 && (down.getId() == GRASS || down.getId() == DIRT)) {
            this.getLevel().setBlock(block, this, true, false); // If we update the bottom half, it will drop the item because there isn't a flower block above
            this.getLevel().setBlock(up, new BlockDoublePlant(meta ^ 0x08), true, true);
            return true;
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        Block down = down();

        if ((this.meta & 0x08) == 0x08) { // Top half
            this.getLevel().useBreakOn(down);
        } else {
            this.getLevel().setBlock(this, new BlockAir(), true, true);
        }

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if ((this.meta & 0x08) != 0x08) {
            switch (this.meta & 0x07) {
                case 2:
                case 3:
                    boolean dropSeeds = new Random().nextInt(10) == 0;
                    if (item.isShears()) {
                        //todo enchantment
                        if (dropSeeds) {
                            return new Item[]{
                                new ItemSeedsWheat(0, 1),
                                toItem()
                            };
                        } else {
                            return new Item[]{
                                toItem()
                            };
                        }
                    }

                    if (dropSeeds) {
                        return new Item[]{
                            new ItemSeedsWheat()
                        };
                    } else {
                        return new Item[0];
                    }
            }

            return new Item[]{toItem()};
        }

        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

}