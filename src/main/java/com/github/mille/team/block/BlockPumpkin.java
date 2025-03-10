package com.github.mille.team.block;

import com.github.mille.team.Player;
import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemTool;
import com.github.mille.team.math.BlockFace;
import com.github.mille.team.utils.BlockColor;

/**
 * Created on 2015/12/8 by xtypr. Package com.github.mille.team.block in project Nukkit .
 */
public class BlockPumpkin extends BlockSolid {

    public BlockPumpkin() {
        this(0);
    }

    public BlockPumpkin(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Pumpkin";
    }

    @Override
    public int getId() {
        return PUMPKIN;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
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
        this.meta = player != null ? player.getDirection().getOpposite().getHorizontalIndex() : 0;
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

}
