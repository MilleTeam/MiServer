package com.github.mille.team.level.generator.populator;

import com.github.mille.team.level.ChunkManager;
import com.github.mille.team.math.NukkitRandom;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class Populator {

    public abstract void populate(
        ChunkManager level,
        int chunkX,
        int chunkZ,
        NukkitRandom random
    );

}
