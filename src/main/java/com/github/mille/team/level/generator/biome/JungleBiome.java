package com.github.mille.team.level.generator.biome;

import com.github.mille.team.level.generator.populator.PopulatorGrass;
import com.github.mille.team.level.generator.populator.tree.JungleBigTreePopulator;
import com.github.mille.team.level.generator.populator.tree.JungleTreePopulator;

public class JungleBiome extends GrassyBiome {

    public JungleBiome() {
        super();
        JungleTreePopulator trees = new JungleTreePopulator();
        JungleBigTreePopulator bigTrees = new JungleBigTreePopulator();
        trees.setBaseAmount(10);
        bigTrees.setBaseAmount(6);
        //PopulatorTallGrass tallGrass = new PopulatorTallGrass();

        PopulatorGrass grass = new PopulatorGrass();
        grass.setBaseAmount(20);

        //PopulatorFern fern = new PopulatorFern();
        //fern.setBaseAmount(30);

        this.addPopulator(grass);
        //this.addPopulator(fern);
        this.addPopulator(bigTrees);
        this.addPopulator(trees);
        this.setElevation(62, 63);
        this.temperature = 1.2f;
        this.rainfall = 0.9f;
    }

    @Override
    public String getName() {
        return "Jungle";
    }

}
