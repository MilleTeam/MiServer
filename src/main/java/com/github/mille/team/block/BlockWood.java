package com.github.mille.team.block;

import com.github.mille.team.Player;
import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemBlock;
import com.github.mille.team.item.ItemTool;
import com.github.mille.team.math.BlockFace;
import com.github.mille.team.utils.BlockColor;

/**
 * author: MagicDroidX Nukkit Project
 */
public class BlockWood extends BlockSolid {

    public static final int OAK = 0;

    public static final int SPRUCE = 1;

    public static final int BIRCH = 2;

    public static final int JUNGLE = 3;


    public BlockWood() {
        this(0);
    }

    public BlockWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WOOD;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
            "Oak Wood",
            "Spruce Wood",
            "Birch Wood",
            "Jungle Wood"
        };

        return names[this.meta & 0x03];
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 10;
    }

    @Override
    public boolean place(
        Item item,
        Block block,
        Block target,
        BlockFace face,
        double fx,
        double fy,
        double fz
    ) {
        return this.place(item, block, target, face, fx, fy, fz, null);
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
        short[] faces = new short[]{
            0,
            0,
            0b1000,
            0b1000,
            0b0100,
            0b0100
        };

        this.meta = ((this.meta & 0x03) | faces[face.getIndex()]);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, this.meta & 0x03);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

}
