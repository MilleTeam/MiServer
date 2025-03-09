package com.github.mille.team.level.generator.task;

import com.github.mille.team.block.Block;
import com.github.mille.team.level.Level;
import com.github.mille.team.level.SimpleChunkManager;
import com.github.mille.team.level.generator.Generator;
import com.github.mille.team.level.generator.biome.Biome;
import com.github.mille.team.math.NukkitRandom;
import com.github.mille.team.scheduler.AsyncTask;

import java.util.Map;

/**
 * author: MagicDroidX Nukkit Project
 */
public class GeneratorRegisterTask extends AsyncTask {

    public final Class<? extends Generator> generator;

    public final Map<String, Object> settings;

    public final long seed;

    public final int levelId;

    public GeneratorRegisterTask(
        Level level,
        Generator generator
    ) {
        this.generator = generator.getClass();
        this.settings = generator.getSettings();
        this.seed = level.getSeed();
        this.levelId = level.getId();
    }

    @Override
    public void onRun() {
        Block.init();
        Biome.init();
        SimpleChunkManager manager = new SimpleChunkManager(this.seed);
        try {
            Generator generator = this.generator.getConstructor(Map.class).newInstance(this.settings);
            generator.init(manager, new NukkitRandom(manager.getSeed()));
            GeneratorPool.put(this.levelId, generator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
