package com.github.mille.team.level.generator.task;

import com.github.mille.team.Server;
import com.github.mille.team.level.Level;
import com.github.mille.team.level.SimpleChunkManager;
import com.github.mille.team.level.format.generic.BaseFullChunk;
import com.github.mille.team.level.generator.Generator;
import com.github.mille.team.scheduler.AsyncTask;

/**
 * author: MagicDroidX Nukkit Project
 */
public class GenerationTask extends AsyncTask {

    public final int levelId;

    public boolean state;

    public BaseFullChunk chunk;

    public GenerationTask(
        Level level,
        BaseFullChunk chunk
    ) {
        this.state = true;
        this.levelId = level.getId();
        this.chunk = chunk;
    }

    @Override
    public void onRun() {
        Generator generator = GeneratorPool.get(this.levelId);

        if (generator == null) {
            this.state = false;
            return;
        }

        SimpleChunkManager manager = (SimpleChunkManager) generator.getChunkManager();

        if (manager == null) {
            this.state = false;
            return;
        }

        synchronized (manager) {
            BaseFullChunk chunk = this.chunk.clone();

            if (chunk == null) {
                return;
            }

            manager.setChunk(chunk.getX(), chunk.getZ(), chunk);

            generator.generateChunk(chunk.getX(), chunk.getZ());

            chunk = manager.getChunk(chunk.getX(), chunk.getZ());
            chunk.setGenerated();
            this.chunk = chunk.clone();

            manager.setChunk(chunk.getX(), chunk.getZ(), null);
        }

    }

    @Override
    public void onCompletion(Server server) {
        Level level = server.getLevel(this.levelId);
        if (level != null) {
            if (!this.state) {
                level.registerGenerator();
                return;
            }

            BaseFullChunk chunk = this.chunk.clone();

            if (chunk == null) {
                return;
            }

            level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk);
        }
    }

}
