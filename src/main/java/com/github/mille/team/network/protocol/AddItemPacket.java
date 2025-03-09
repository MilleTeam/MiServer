package com.github.mille.team.network.protocol;

import com.github.mille.team.item.Item;

public class AddItemPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ADD_ITEM_PACKET;

    public Item item;

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
        this.putSlot(item);
    }

}
