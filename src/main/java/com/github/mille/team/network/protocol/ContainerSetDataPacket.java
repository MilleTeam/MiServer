package com.github.mille.team.network.protocol;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ContainerSetDataPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CONTAINER_SET_DATA_PACKET;

    public byte windowid;

    public int property;

    public int value;

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
        this.putByte(this.windowid);
        this.putVarInt(this.property);
        this.putVarInt(this.value);
    }

}
