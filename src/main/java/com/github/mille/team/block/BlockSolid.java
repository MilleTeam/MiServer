package com.github.mille.team.block;

import com.github.mille.team.utils.BlockColor;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class BlockSolid extends Block {

    protected BlockSolid(int meta) {
        super(meta);
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

}
