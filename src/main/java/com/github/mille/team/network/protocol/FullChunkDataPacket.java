package com.github.mille.team.network.protocol;

/**
 * author: MagicDroidX Nukkit Project
 */
public class FullChunkDataPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.FULL_CHUNK_DATA_PACKET;

    public int chunkX;

    public int chunkZ;

    public byte[] data;

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
        this.putVarInt(this.chunkX);
        this.putVarInt(this.chunkZ);
        this.putByteArray(this.data);
    }

}
