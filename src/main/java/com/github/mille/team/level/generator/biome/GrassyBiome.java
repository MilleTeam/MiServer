package com.github.mille.team.level.generator.biome;

import com.github.mille.team.block.Block;
import com.github.mille.team.block.BlockDirt;
import com.github.mille.team.block.BlockGrass;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class GrassyBiome extends NormalBiome implements CaveBiome {

    public GrassyBiome() {
        this.setGroundCover(new Block[]{
            new BlockGrass(),
            new BlockDirt(),
            new BlockDirt(),
            new BlockDirt(),
            new BlockDirt()
        });
    }

    @Override
    public int getSurfaceBlock() {
        return Block.GRASS;
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
