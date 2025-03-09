package com.github.mille.team.level.format;

import com.github.mille.team.level.GameRules;
import com.github.mille.team.level.Level;
import com.github.mille.team.level.format.generic.BaseFullChunk;
import com.github.mille.team.math.Vector3;
import com.github.mille.team.scheduler.AsyncTask;

import java.util.Map;

/**
 * author: MagicDroidX Nukkit Project
 */
public interface LevelProvider {

    byte ORDER_YZX = 0;

    byte ORDER_ZXY = 1;

    AsyncTask requestChunkTask(
        int x,
        int z
    );

    String getPath();

    String getGenerator();

    Map<String, Object> getGeneratorOptions();

    BaseFullChunk getChunk(
        int x,
        int z
    );

    BaseFullChunk getChunk(
        int x,
        int z,
        boolean create
    );

    void saveChunks();

    void saveChunk(
        int x,
        int z
    );

    void saveChunk(
        int x,
        int z,
        FullChunk chunk
    );

    void unloadChunks();

    boolean loadChunk(
        int x,
        int z
    );

    boolean loadChunk(
        int x,
        int z,
        boolean create
    );

    boolean unloadChunk(
        int x,
        int z
    );

    boolean unloadChunk(
        int x,
        int z,
        boolean safe
    );

    boolean isChunkGenerated(
        int x,
        int z
    );

    boolean isChunkPopulated(
        int x,
        int z
    );

    boolean isChunkLoaded(
        int x,
        int z
    );

    void setChunk(
        int chunkX,
        int chunkZ,
        FullChunk chunk
    );

    String getName();

    boolean isRaining();

    void setRaining(boolean raining);

    int getRainTime();

    void setRainTime(int rainTime);

    boolean isThundering();

    void setThundering(boolean thundering);

    int getThunderTime();

    void setThunderTime(int thunderTime);

    long getCurrentTick();

    void setCurrentTick(long currentTick);

    long getTime();

    void setTime(long value);

    long getSeed();

    void setSeed(long value);

    Vector3 getSpawn();

    void setSpawn(Vector3 pos);

    Map<Long, ? extends FullChunk> getLoadedChunks();

    void doGarbageCollection();

    Level getLevel();

    void close();

    void saveLevelData();

    void updateLevelName(String name);

    GameRules getGamerules();

    void setGameRules(GameRules rules);

}
