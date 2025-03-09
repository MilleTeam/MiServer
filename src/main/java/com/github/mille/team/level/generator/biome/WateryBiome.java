package com.github.mille.team.level.generator.biome;

import com.github.mille.team.block.Block;
import com.github.mille.team.block.BlockDirt;

/**
 * author: Angelic47 Nukkit Project
 */
public abstract class WateryBiome extends NormalBiome implements CaveBiome {

    public WateryBiome() {
        this.setGroundCover(new Block[]{
            new BlockDirt(),
            new BlockDirt(),
            new BlockDirt(),
            new BlockDirt(),
            new BlockDirt()
        });
    }

    @Override
    public int getSurfaceBlock() {
        return Block.DIRT;
    }

    @Override
    public int getGroundBlock() {
        return Block.DIRT;
    }

    @Override
    public int getStoneBlock() {
        return Block.STONE;
    }

}
