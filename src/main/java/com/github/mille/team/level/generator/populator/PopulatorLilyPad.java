package com.github.mille.team.level.generator.populator;

import com.github.mille.team.block.Block;
import com.github.mille.team.level.ChunkManager;
import com.github.mille.team.math.NukkitMath;
import com.github.mille.team.math.NukkitRandom;

public class PopulatorLilyPad extends Populator {

    /**
     * Author: Niall Lindsay <Niall7459>
     */

    private ChunkManager level;

    private int randomAmount;

    private int baseAmount;

    public void setRandomAmount(int randomAmount) {
        this.randomAmount = randomAmount;
    }

    public void setBaseAmount(int baseAmount) {
        this.baseAmount = baseAmount;
    }

    @Override
    public void populate(
        ChunkManager level,
        int chunkX,
        int chunkZ,
        NukkitRandom random
    ) {
        this.level = level;
        int amount = random.nextBoundedInt(this.randomAmount + 1) + this.baseAmount;
        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX * 16, chunkX * 16 + 15);
            int z = NukkitMath.randomRange(random, chunkZ * 16, chunkZ * 16 + 15);
            int y = this.getHighestWorkableBlock(x, z);

            if (y != -1 && this.canLilyPadStay(x, y, z)) {
                this.level.setBlockIdAt(x, y, z, Block.WATER_LILY);
                this.level.setBlockDataAt(x, y, z, 1);
            }
        }
    }

    private boolean canLilyPadStay(
        int x,
        int y,
        int z
    ) {
        int b = this.level.getBlockIdAt(x, y, z);
        return (b == Block.AIR || b == Block.SNOW_LAYER) && this.level.getBlockIdAt(x, y - 1, z) == Block.STILL_WATER;
    }

    private int getHighestWorkableBlock(
        int x,
        int z
    ) {
        int y;
        for (y = 127; y >= 0; --y) {
            int b = this.level.getBlockIdAt(x, y, z);
            if (b != Block.AIR && b != Block.LEAVES && b != Block.LEAVES2 && b != Block.SNOW_LAYER) {
                break;
            }
        }

        return y == 0 ? -1 : ++y;
    }

}
