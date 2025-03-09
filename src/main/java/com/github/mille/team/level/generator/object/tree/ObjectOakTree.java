package com.github.mille.team.level.generator.object.tree;

import com.github.mille.team.block.Block;
import com.github.mille.team.block.BlockWood;
import com.github.mille.team.level.ChunkManager;
import com.github.mille.team.math.NukkitRandom;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ObjectOakTree extends ObjectTree {

    private int treeHeight = 7;

    @Override
    public int getTrunkBlock() {
        return Block.LOG;
    }

    @Override
    public int getLeafBlock() {
        return Block.LEAVES;
    }

    @Override
    public int getType() {
        return BlockWood.OAK;
    }

    @Override
    public int getTreeHeight() {
        return this.treeHeight;
    }

    @Override
    public void placeObject(
        ChunkManager level,
        int x,
        int y,
        int z,
        NukkitRandom random
    ) {
        this.treeHeight = random.nextBoundedInt(3) + 4;
        super.placeObject(level, x, y, z, random);
    }

}
