package com.github.mille.team.level.sound;

import com.github.mille.team.math.Vector3;
import com.github.mille.team.network.protocol.DataPacket;
import com.github.mille.team.network.protocol.LevelEventPacket;

/**
 * Created on 2015/11/21 by xtypr. Package com.github.mille.team.level.sound in project Nukkit .
 */
public class LevelEventSound extends Sound {

    protected final int id;

    protected float pitch = 0f;

    public LevelEventSound(
        Vector3 pos,
        int id
    ) {
        this(pos, id, 0);
    }

    public LevelEventSound(
        Vector3 pos,
        int id,
        float pitch
    ) {
        super(pos.x, pos.y, pos.z);
        this.id = id;
        this.pitch = pitch * 1000f;
    }

    public float getPitch() {
        return this.pitch / 1000f;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch * 1000f;
    }

    @Override
    public DataPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = this.id;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.data = (int) this.pitch;

        return new DataPacket[]{pk};
    }

}
