package com.github.mille.team.level.sound;

import com.github.mille.team.math.Vector3;
import com.github.mille.team.network.protocol.LevelEventPacket;

public class ExplodeSound extends LevelEventSound {

    public ExplodeSound(Vector3 pos) {
        this(pos, 0);
    }

    public ExplodeSound(
        Vector3 pos,
        float pitch
    ) {
        super(pos, LevelEventPacket.EVENT_SOUND_EXPLODE, pitch);
    }

}
