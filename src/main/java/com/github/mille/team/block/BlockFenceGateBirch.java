package com.github.mille.team.block;

/**
 * Created on 2015/11/23 by xtypr. Package com.github.mille.team.block in project Nukkit .
 */
public class BlockFenceGateBirch extends BlockFenceGate {

    public BlockFenceGateBirch() {
        this(0);
    }

    public BlockFenceGateBirch(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FENCE_GATE_BIRCH;
    }

    @Override
    public String getName() {
        return "Birch Fence Gate";
    }

}
