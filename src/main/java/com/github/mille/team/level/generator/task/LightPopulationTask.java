package com.github.mille.team.level.generator.task;

import com.github.mille.team.Server;
import com.github.mille.team.level.Level;
import com.github.mille.team.level.format.generic.BaseFullChunk;
import com.github.mille.team.scheduler.AsyncTask;

/**
 * author: MagicDroidX Nukkit Project
 */
public class LightPopulationTask extends AsyncTask {

    public final int levelId;

    public BaseFullChunk chunk;

    public LightPopulationTask(
        Level level,
        BaseFullChunk chunk
    ) {
        this.levelId = level.getId();
        this.chunk = chunk;
    }

    @Override
    public void onRun() {
        BaseFullChunk chunk = this.chunk.clone();
        if (chunk == null) {
            return;
        }

        chunk.recalculateHeightMap();
        chunk.populateSkyLight();
        chunk.setLightPopulated();

        this.chunk = chunk.clone();
    }

    @Override
    public void onCompletion(Server server) {
        Level level = server.getLevel(this.levelId);

        BaseFullChunk chunk = this.chunk.clone();
        if (level != null) {
            if (chunk == null) {
                return;
            }

            level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk);
        }
    }

}
