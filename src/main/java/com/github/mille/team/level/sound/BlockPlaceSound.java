package com.github.mille.team.level.sound;

import com.github.mille.team.math.Vector3;
import com.github.mille.team.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class BlockPlaceSound extends LevelSoundEventSound {

    public BlockPlaceSound(
        Vector3 pos,
        int blockId
    ) {
        super(pos, LevelSoundEventPacket.SOUND_PLACE, blockId, 1);
    }

}