package com.github.mille.team.level.sound;

import com.github.mille.team.math.Vector3;
import com.github.mille.team.network.protocol.LevelEventPacket;

/**
 * Created by Pub4Game on 04.03.2016.
 */
public class TNTPrimeSound extends LevelEventSound {

    public TNTPrimeSound(Vector3 pos) {
        this(pos, 0);
    }

    public TNTPrimeSound(
        Vector3 pos,
        float pitch
    ) {
        super(pos, LevelEventPacket.EVENT_SOUND_TNT, pitch);
    }

}
