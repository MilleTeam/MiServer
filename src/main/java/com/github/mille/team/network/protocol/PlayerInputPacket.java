package com.github.mille.team.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class PlayerInputPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_INPUT_PACKET;

    public float motionX;

    public float motionY;

    public boolean unknownBool1;

    public boolean unknownBool2;

    @Override
    public void decode() {
        this.motionX = this.getLFloat();
        this.motionY = this.getLFloat();
        this.unknownBool1 = this.getBoolean();
        this.unknownBool2 = this.getBoolean();
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
