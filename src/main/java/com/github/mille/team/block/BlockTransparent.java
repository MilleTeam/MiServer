package com.github.mille.team.block;

import com.github.mille.team.utils.BlockColor;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class BlockTransparent extends Block {

    protected BlockTransparent(int meta) {
        super(meta);
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.TRANSPARENT_BLOCK_COLOR;
    }

}
