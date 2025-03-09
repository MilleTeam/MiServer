package com.github.mille.team.level.generator.task;

import com.github.mille.team.level.Level;
import com.github.mille.team.scheduler.AsyncTask;

/**
 * author: MagicDroidX Nukkit Project
 */
public class GeneratorUnregisterTask extends AsyncTask {

    public final int levelId;

    public GeneratorUnregisterTask(Level level) {
        this.levelId = level.getId();
    }

    @Override
    public void onRun() {
        GeneratorPool.remove(levelId);
    }

}
