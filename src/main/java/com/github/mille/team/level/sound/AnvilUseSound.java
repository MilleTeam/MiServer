package com.github.mille.team.level.sound;

import com.github.mille.team.math.Vector3;
import com.github.mille.team.network.protocol.LevelEventPacket;

/**
 * Created on 2015/11/21 by xtypr. Package com.github.mille.team.level.sound in project Nukkit .
 */
public class AnvilUseSound extends LevelEventSound {

    public AnvilUseSound(Vector3 pos) {
        this(pos, 0);
    }

    public AnvilUseSound(
        Vector3 pos,
        float pitch
    ) {
        super(pos, LevelEventPacket.EVENT_SOUND_ANVIL_USE, pitch);
    }

}
