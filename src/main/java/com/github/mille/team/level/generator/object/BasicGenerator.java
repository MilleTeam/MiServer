package com.github.mille.team.level.generator.object;

import com.github.mille.team.block.Block;
import com.github.mille.team.level.ChunkManager;
import com.github.mille.team.math.BlockVector3;
import com.github.mille.team.math.NukkitRandom;
import com.github.mille.team.math.Vector3;

public abstract class BasicGenerator {

    public abstract boolean generate(
        ChunkManager level,
        NukkitRandom rand,
        Vector3 position
    );

    public void setDecorationDefaults() {
    }

    protected void setBlockAndNotifyAdequately(
        ChunkManager level,
        BlockVector3 pos,
        Block state
    ) {
        setBlock(level, new Vector3(pos.x, pos.y, pos.z), state);
    }

    protected void setBlockAndNotifyAdequately(
        ChunkManager level,
        Vector3 pos,
        Block state
    ) {
        setBlock(level, pos, state);
    }

    protected void setBlock(
        ChunkManager level,
        Vector3 v,
        Block b
    ) {
        level.setBlockIdAt((int) v.x, (int) v.y, (int) v.z, b.getId());
        level.setBlockDataAt((int) v.x, (int) v.y, (int) v.z, b.getDamage());
    }

}
