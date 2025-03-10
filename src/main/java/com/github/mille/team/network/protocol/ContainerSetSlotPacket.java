package com.github.mille.team.network.protocol;

import com.github.mille.team.item.Item;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ContainerSetSlotPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CONTAINER_SET_SLOT_PACKET;

    public int windowid;

    public int slot;

    public int hotbarSlot;

    public Item item;

    public int selectedSlot;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.windowid = this.getByte();
        this.slot = this.getVarInt();
        this.hotbarSlot = this.getVarInt();
        this.item = this.getSlot();
        this.selectedSlot = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.windowid);
        this.putVarInt(this.slot);
        this.putVarInt(this.hotbarSlot);
        this.putSlot(this.item);
        this.putByte((byte) this.selectedSlot);
    }

}
