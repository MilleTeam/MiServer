package com.github.mille.team.level.generator.populator.tree;

import com.github.mille.team.block.Block;
import com.github.mille.team.block.BlockSapling;
import com.github.mille.team.level.ChunkManager;
import com.github.mille.team.level.generator.object.tree.NewJungleTree;
import com.github.mille.team.level.generator.populator.Populator;
import com.github.mille.team.math.NukkitMath;
import com.github.mille.team.math.NukkitRandom;
import com.github.mille.team.math.Vector3;

public class JungleTreePopulator extends Populator {

    private final int type;

    private ChunkManager level;

    private int randomAmount;

    private int baseAmount;

    public JungleTreePopulator() {
        this(BlockSapling.JUNGLE);
    }

    public JungleTreePopulator(int type) {
        this.type = type;
    }

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
        Vector3 v = new Vector3();

        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            int y = this.getHighestWorkableBlock(x, z);
            if (y == -1) {
                continue;
            }
            new NewJungleTree(4 + random.nextBoundedInt(7)).generate(level, random, v.setComponents(x, y, z));
        }
    }

    private int getHighestWorkableBlock(
        int x,
        int z
    ) {
        int y;
        for (y = 127; y > 0; --y) {
            int b = this.level.getBlockIdAt(x, y, z);
            if (b == Block.DIRT || b == Block.GRASS) {
                break;
            } else if (b != Block.AIR && b != Block.SNOW_LAYER) {
                return -1;
            }
        }

        return ++y;
    }

}
