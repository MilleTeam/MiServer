package com.github.mille.team.raknet.protocol.packet;

import com.github.mille.team.raknet.protocol.Packet;

/**
 * author: MagicDroidX Nukkit Project
 */
public class PONG_DataPacket extends Packet {

    public static final byte ID = (byte) 0x03;

    public long pingID;

    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public void encode() {
        super.encode();
        this.putLong(this.pingID);
    }

    @Override
    public void decode() {
        super.decode();
        this.pingID = this.getLong();
    }

    public static final class Factory implements Packet.PacketFactory {

        @Override
        public Packet create() {
            return new PONG_DataPacket();
        }

    }

}
