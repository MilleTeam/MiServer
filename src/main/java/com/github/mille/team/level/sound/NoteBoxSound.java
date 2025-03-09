package com.github.mille.team.level.sound;

import com.github.mille.team.math.Vector3;
import com.github.mille.team.network.protocol.LevelSoundEventPacket;

/**
 * author: MagicDroidX Nukkit Project
 */

public class NoteBoxSound extends LevelSoundEventSound {

    public static final int INSTRUMENT_PIANO = 0;

    public static final int INSTRUMENT_BASS_DRUM = 1;

    public static final int INSTRUMENT_CLICK = 2;

    public static final int INSTRUMENT_TABOUR = 3;

    public static final int INSTRUMENT_BASS = 4;

    public NoteBoxSound(
        Vector3 pos,
        int instrument,
        int pitch
    ) {
        super(pos, LevelSoundEventPacket.SOUND_NOTE, instrument, pitch);
    }

}
