package com.github.mille.team.level.sound;

import com.github.mille.team.math.Vector3;
import com.github.mille.team.network.protocol.DataPacket;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class Sound extends Vector3 {

    public Sound() {
        super(0, 0, 0);
    }

    public Sound(double x) {
        super(x, 0, 0);
    }

    public Sound(
        double x,
        double y
    ) {
        super(x, y, 0);
    }

    public Sound(
        double x,
        double y,
        double z
    ) {
        super(x, y, z);
    }

    abstract public DataPacket[] encode();

}
