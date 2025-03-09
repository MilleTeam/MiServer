package com.github.mille.team.level.sound;

import com.github.mille.team.math.Vector3;
import com.github.mille.team.network.protocol.LevelEventPacket;

public class SplashSound extends LevelEventSound {

    public SplashSound(Vector3 pos) {
        this(pos, 0);
    }

    public SplashSound(
        Vector3 pos,
        float pitch
    ) {
        super(pos, LevelEventPacket.EVENT_SOUND_SPLASH, pitch);
    }

}
