package com.github.mille.team.level.sound;

import com.github.mille.team.math.Vector3;
import com.github.mille.team.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class PistonInSound extends LevelSoundEventSound {

    public PistonInSound(Vector3 pos) {
        super(pos, LevelSoundEventPacket.SOUND_PISTON_IN, -1, 1);
    }

}
