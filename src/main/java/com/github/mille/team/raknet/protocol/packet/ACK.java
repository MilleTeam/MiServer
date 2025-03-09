package com.github.mille.team.raknet.protocol.packet;

import com.github.mille.team.raknet.protocol.AcknowledgePacket;
import com.github.mille.team.raknet.protocol.Packet;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ACK extends AcknowledgePacket {

    public static final byte ID = (byte) 0xc0;

    @Override
    public byte getID() {
        return ID;
    }

    public static final class Factory implements Packet.PacketFactory {

        @Override
        public Packet create() {
            return new ACK();
        }

    }

}
