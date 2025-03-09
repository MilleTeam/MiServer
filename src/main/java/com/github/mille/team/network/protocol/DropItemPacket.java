package com.github.mille.team.network.protocol;

import com.github.mille.team.item.Item;

/**
 * @author Nukkit Project Team
 */
public class DropItemPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.DROP_ITEM_PACKET;

    public int type;

    public Item item;

    @Override
    public void decode() {
        this.type = this.getByte();
        this.item = this.getSlot();
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
