package com.github.mille.team.raknet.protocol.packet;

import com.github.mille.team.raknet.protocol.Packet;

/**
 * author: MagicDroidX Nukkit Project
 */
public class CLIENT_DISCONNECT_DataPacket extends Packet {

    public static final byte ID = (byte) 0x15;

    @Override
    public byte getID() {
        return ID;
    }

    public static final class Factory implements Packet.PacketFactory {

        @Override
        public Packet create() {
            return new CLIENT_DISCONNECT_DataPacket();
        }

    }

}
