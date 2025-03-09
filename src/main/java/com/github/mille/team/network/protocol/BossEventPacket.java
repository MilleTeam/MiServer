package com.github.mille.team.network.protocol;

/**
 * Created by CreeperFace on 30. 10. 2016.
 */
public class BossEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.BOSS_EVENT_PACKET;

    public static final int ADD = 0;

    public static final int UPDATE = 1;

    public static final int REMOVE = 2;

    public long eid;

    public int type;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putVarLong(this.eid);
        this.putUnsignedVarInt(this.type);
    }

}
