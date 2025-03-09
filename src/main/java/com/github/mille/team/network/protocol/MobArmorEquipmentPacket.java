package com.github.mille.team.network.protocol;

import com.github.mille.team.item.Item;

/**
 * author: MagicDroidX Nukkit Project
 */
public class MobArmorEquipmentPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET;

    public long eid;

    public Item[] slots = new Item[4];

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.eid = this.getVarLong();
        this.slots = new Item[4];
        this.slots[0] = this.getSlot();
        this.slots[1] = this.getSlot();
        this.slots[2] = this.getSlot();
        this.slots[3] = this.getSlot();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarLong(this.eid);
        this.putSlot(this.slots[0]);
        this.putSlot(this.slots[1]);
        this.putSlot(this.slots[2]);
        this.putSlot(this.slots[3]);
    }

}
