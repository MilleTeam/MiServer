package com.github.mille.team.block;

import com.github.mille.team.utils.BlockColor;

/**
 * author: Angelic47 Nukkit Project
 */
public class BlockSponge extends BlockSolid {

    public static final int DRY = 0;

    public static final int WET = 1;

    public BlockSponge() {
        this(0);
    }

    public BlockSponge(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SPONGE;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
            "Sponge",
            "Wet sponge"
        };
        return names[this.meta & 0x07];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CLOTH_BLOCK_COLOR;
    }

}
