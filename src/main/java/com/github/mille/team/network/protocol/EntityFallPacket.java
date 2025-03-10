package com.github.mille.team.network.protocol;

public class EntityFallPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ENTITY_FALL_PACKET;

    public long eid;

    public float fallDistance;

    public boolean unknown;

    @Override
    public void decode() {
        this.eid = this.getVarLong();
        this.fallDistance = this.getLFloat();
        this.unknown = this.getBoolean();
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
