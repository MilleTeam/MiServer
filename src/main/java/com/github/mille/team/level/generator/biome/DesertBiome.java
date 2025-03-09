package com.github.mille.team.level.generator.biome;

/**
 * author: MagicDroidX Nukkit Project
 */
public class DesertBiome extends SandyBiome {

    public DesertBiome() {
        super();
        this.setElevation(63, 74);
        this.temperature = 2;
        this.rainfall = 0;
    }

    @Override
    public String getName() {
        return "Desert";
    }

}
