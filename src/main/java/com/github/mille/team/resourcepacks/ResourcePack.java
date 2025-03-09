package com.github.mille.team.resourcepacks;

public interface ResourcePack {

    String getPackName();

    String getPackId();

    String getPackVersion();

    int getPackSize();

    byte[] getSha256();

    byte[] getPackChunk(
        int off,
        int len
    );

}
