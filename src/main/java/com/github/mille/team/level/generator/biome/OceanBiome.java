package com.github.mille.team.level.generator.biome;

import com.github.mille.team.block.Block;
import com.github.mille.team.level.generator.populator.PopulatorSugarcane;
import com.github.mille.team.level.generator.populator.PopulatorTallSugarcane;

/**
 * author: MagicDroidX Nukkit Project
 */
public class OceanBiome extends WateryBiome {

    public OceanBiome() {
        super();

        PopulatorSugarcane sugarcane = new PopulatorSugarcane();
        sugarcane.setBaseAmount(6);
        PopulatorTallSugarcane tallSugarcane = new PopulatorTallSugarcane();
        tallSugarcane.setBaseAmount(60);
        this.addPopulator(sugarcane);
        this.addPopulator(tallSugarcane);
        this.setElevation(46, 58);

        this.temperature = 0.5;
        this.rainfall = 0.5;

    }

    @Override
    public Block[] getGroundCover() {
        return super.getGroundCover();
    }

    @Override
    public String getName() {
        return "Ocean";
    }

}
