package com.github.mille.team.level.sound;

import com.github.mille.team.math.Vector3;
import com.github.mille.team.network.protocol.LevelEventPacket;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class ItemFramePlacedSound extends LevelEventSound {

    public ItemFramePlacedSound(Vector3 pos) {
        this(pos, 0);
    }

    public ItemFramePlacedSound(
        Vector3 pos,
        float pitch
    ) {
        super(pos, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_PLACED, pitch);
    }

}
