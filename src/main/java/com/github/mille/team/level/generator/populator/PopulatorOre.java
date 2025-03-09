package com.github.mille.team.level.generator.populator;

import com.github.mille.team.block.Block;
import com.github.mille.team.level.ChunkManager;
import com.github.mille.team.level.generator.object.ore.ObjectOre;
import com.github.mille.team.level.generator.object.ore.OreType;
import com.github.mille.team.math.NukkitMath;
import com.github.mille.team.math.NukkitRandom;

/**
 * author: MagicDroidX Nukkit Project
 */
public class PopulatorOre extends Populator {

    private final int replaceId;

    private OreType[] oreTypes = new OreType[0];

    public PopulatorOre() {
        this(Block.STONE);
    }

    public PopulatorOre(int id) {
        this.replaceId = id;
    }

    @Override
    public void populate(
        ChunkManager level,
        int chunkX,
        int chunkZ,
        NukkitRandom random
    ) {
        for (OreType type : this.oreTypes) {
            ObjectOre ore = new ObjectOre(random, type, replaceId);
            for (int i = 0; i < ore.type.clusterCount; ++i) {
                int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
                int y = NukkitMath.randomRange(random, ore.type.minHeight, ore.type.maxHeight);
                int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
                if (ore.canPlaceObject(level, x, y, z)) {
                    ore.placeObject(level, x, y, z);
                }
            }
        }
    }

    public void setOreTypes(OreType[] oreTypes) {
        this.oreTypes = oreTypes;
    }

}
