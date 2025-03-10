package com.github.mille.team.block;

import com.github.mille.team.item.Item;
import com.github.mille.team.utils.BlockColor;

/**
 * @author PikyCZ
 */
public class BlockEndGateway extends BlockSolid {

    public BlockEndGateway() {
        this(0);
    }

    public BlockEndGateway(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "End Gateway";
    }

    @Override
    public int getId() {
        return END_GATEWAY;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

}
