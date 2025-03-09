package com.github.mille.team.level.sound;

import com.github.mille.team.math.Vector3;
import com.github.mille.team.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class PistonOutSound extends LevelSoundEventSound {

    public PistonOutSound(Vector3 pos) {
        super(pos, LevelSoundEventPacket.SOUND_PISTON_OUT, -1, 1);
    }

}
