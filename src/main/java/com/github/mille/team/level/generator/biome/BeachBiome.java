package com.github.mille.team.level.generator.biome;

import com.github.mille.team.block.Block;
import com.github.mille.team.block.BlockSand;
import com.github.mille.team.block.BlockSandstone;

/**
 * Author: PeratX Nukkit Project
 */
public class BeachBiome extends SandyBiome {

    public BeachBiome() {
        //Todo: SugarCane

        this.setElevation(62, 65);
        this.temperature = 2;
        this.rainfall = 0;

        this.setGroundCover(new Block[]{
            new BlockSand(),
            new BlockSand(),
            new BlockSandstone(),
            new BlockSandstone(),
            new BlockSandstone()
        });
    }

    @Override
    public String getName() {
        return "Beach";
    }

}
