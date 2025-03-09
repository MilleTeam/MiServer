package com.github.mille.team.level.generator.object.tree;

import com.github.mille.team.block.BlockDirt;
import com.github.mille.team.item.Item;
import com.github.mille.team.level.ChunkManager;
import com.github.mille.team.level.Level;
import com.github.mille.team.math.BlockVector3;
import com.github.mille.team.math.Vector3;

import java.util.Random;

public abstract class TreeGenerator extends com.github.mille.team.level.generator.object.BasicGenerator {

    /**
     * returns whether or not a tree can grow into a block For example, a tree will not grow into stone
     */
    protected boolean canGrowInto(int id) {
        return id == Item.AIR || id == Item.LEAVES || id == Item.GRASS || id == Item.DIRT || id == Item.LOG || id == Item.LOG2 || id == Item.SAPLING || id == Item.VINE;
    }

    public void generateSaplings(
        Level level,
        Random random,
        Vector3 pos
    ) {
    }

    protected void setDirtAt(
        ChunkManager level,
        BlockVector3 pos
    ) {
        setDirtAt(level, new Vector3(pos.x, pos.y, pos.z));
    }

    /**
     * sets dirt at a specific location if it isn't already dirt
     */
    protected void setDirtAt(
        ChunkManager level,
        Vector3 pos
    ) {
        if (level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z) != Item.DIRT) {
            this.setBlockAndNotifyAdequately(level, pos, new BlockDirt());
        }
    }

}
