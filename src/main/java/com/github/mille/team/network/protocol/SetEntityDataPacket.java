package com.github.mille.team.network.protocol;

import com.github.mille.team.entity.data.EntityMetadata;
import com.github.mille.team.utils.Binary;

/**
 * author: MagicDroidX Nukkit Project
 */
public class SetEntityDataPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_ENTITY_DATA_PACKET;

    public long eid;

    public EntityMetadata metadata;

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
        this.putVarLong(this.eid);
        this.put(Binary.writeMetadata(this.metadata));
    }

}
