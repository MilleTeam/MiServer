package com.github.mille.team.block;

import com.github.mille.team.Player;
import com.github.mille.team.item.Item;
import com.github.mille.team.math.BlockFace;
import com.github.mille.team.utils.BlockColor;

/**
 * Created on 2015/11/24 by xtypr. Package com.github.mille.team.block in project Nukkit .
 */
public class BlockHayBale extends BlockSolid {

    public BlockHayBale() {
        this(0);
    }

    public BlockHayBale(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return HAY_BALE;
    }

    @Override
    public String getName() {
        return "Hay Bale";
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 20;
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
        int[] faces = new int[]{
            0,
            0,
            0b1000,
            0b1000,
            0b0100,
            0b0100,
        };
        this.meta = (this.meta & 0x03) | faces[face.getIndex()];
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRASS_BLOCK_COLOR;
    }

}
