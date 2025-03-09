package com.github.mille.team.level.generator.populator;

import com.github.mille.team.block.Block;
import com.github.mille.team.block.BlockGlowstone;
import com.github.mille.team.level.ChunkManager;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.level.format.generic.BaseFullChunk;
import com.github.mille.team.level.generator.object.ore.ObjectOre;
import com.github.mille.team.level.generator.object.ore.OreType;
import com.github.mille.team.math.NukkitRandom;

public class PopulatorGlowStone extends Populator {

    private final OreType type = new OreType(new BlockGlowstone(), 1, 20, 128, 10);
    private ChunkManager level;

    @Override
    public void populate(
        ChunkManager level,
        int chunkX,
        int chunkZ,
        NukkitRandom random
    ) {
        this.level = level;
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
        int bx = chunkX << 4;
        int bz = chunkZ << 4;
        int tx = bx + 15;
        int tz = bz + 15;
        ObjectOre ore = new ObjectOre(random, type, Block.AIR);
        for (int i = 0; i < ore.type.clusterCount; ++i) {
            int x = random.nextRange(0, 15);
            int z = random.nextRange(0, 15);
            int y = this.getHighestWorkableBlock(chunk, x, z);
            if (y != -1) {
                ore.placeObject(level, bx + x, y, bz + z);
            }
        }
    }

    private int getHighestWorkableBlock(
        FullChunk chunk,
        int x,
        int z
    ) {
        int y;
        for (y = 127; y >= 0; y--) {
            int b = chunk.getBlockId(x, y, z);
            if (b == Block.AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }

}
