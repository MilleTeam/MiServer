package com.github.mille.team.raknet.protocol.packet;

import com.github.mille.team.raknet.protocol.DataPacket;
import com.github.mille.team.raknet.protocol.Packet;

/**
 * author: MagicDroidX Nukkit Project
 */
public class DATA_PACKET_6 extends DataPacket {

    public static final byte ID = (byte) 0x86;

    @Override
    public byte getID() {
        return ID;
    }

    public static final class Factory implements Packet.PacketFactory {

        @Override
        public Packet create() {
            return new DATA_PACKET_6();
        }

    }

}
