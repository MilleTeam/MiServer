package com.github.mille.team.level.sound;

import com.github.mille.team.math.Vector3;
import com.github.mille.team.network.protocol.LevelEventPacket;

/**
 * Created on 2015/11/21 by xtypr. Package com.github.mille.team.level.sound in project Nukkit .
 */
public class AnvilBreakSound extends LevelEventSound {

    public AnvilBreakSound(Vector3 pos) {
        this(pos, 0);
    }

    public AnvilBreakSound(
        Vector3 pos,
        float pitch
    ) {
        super(pos, LevelEventPacket.EVENT_SOUND_ANVIL_BREAK, pitch);
    }

}
