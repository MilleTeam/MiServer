package com.github.mille.team.level.sound;

import com.github.mille.team.math.Vector3;
import com.github.mille.team.network.protocol.LevelEventPacket;

/**
 * author: MagicDroidX Nukkit Project
 */
public class LeverSound extends LevelEventSound {

    public LeverSound(
        Vector3 pos,
        boolean isPowerOn
    ) {
        super(pos, LevelEventPacket.EVENT_SOUND_BUTTON_CLICK, isPowerOn ? 0.6f : 0.5f);
    }

}
