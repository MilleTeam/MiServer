package com.github.mille.team.level.generator.biome;

import com.github.mille.team.block.Block;
import com.github.mille.team.block.BlockSand;
import com.github.mille.team.block.BlockSandstone;
import com.github.mille.team.level.generator.populator.PopulatorCactus;
import com.github.mille.team.level.generator.populator.PopulatorDeadBush;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class SandyBiome extends NormalBiome implements CaveBiome {

    public SandyBiome() {

        PopulatorCactus cactus = new PopulatorCactus();
        cactus.setBaseAmount(2);

        PopulatorDeadBush deadbush = new PopulatorDeadBush();
        deadbush.setBaseAmount(2);

        this.addPopulator(cactus);
        this.addPopulator(deadbush);

        this.setGroundCover(new Block[]{
            new BlockSand(),
            new BlockSand(),
            new BlockSandstone(),
            new BlockSandstone(),
            new BlockSandstone()
        });
    }

    @Override
    public int getSurfaceBlock() {
        return Block.SAND;
    }

    @Override
    public int getGroundBlock() {
        return Block.SAND;
    }

    @Override
    public int getStoneBlock() {
        return Block.SANDSTONE;
    }

}
