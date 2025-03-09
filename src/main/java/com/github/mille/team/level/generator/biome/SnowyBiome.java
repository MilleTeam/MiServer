package com.github.mille.team.level.generator.biome;

import com.github.mille.team.block.Block;
import com.github.mille.team.block.BlockDirt;
import com.github.mille.team.block.BlockGrass;
import com.github.mille.team.block.BlockSnowLayer;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class SnowyBiome extends NormalBiome {

    public SnowyBiome() {
        this.setGroundCover(new Block[]{
            new BlockSnowLayer(),
            new BlockGrass(),
            new BlockDirt(),
            new BlockDirt(),
            new BlockDirt()
        });
    }

}
