package com.github.mille.team.level.sound;

import com.github.mille.team.math.Vector3;
import com.github.mille.team.network.protocol.LevelEventPacket;

/**
 * Created on 2015/11/21 by xtypr. Package com.github.mille.team.level.sound in project Nukkit .
 */
public class GhastSound extends LevelEventSound {

    public GhastSound(Vector3 pos) {
        this(pos, 0);
    }

    public GhastSound(
        Vector3 pos,
        float pitch
    ) {
        super(pos, LevelEventPacket.EVENT_SOUND_GHAST, pitch);
    }

}
