package com.github.mille.team.network.protocol;

import com.github.mille.team.entity.Attribute;
import com.github.mille.team.entity.data.EntityMetadata;
import com.github.mille.team.utils.Binary;

/**
 * author: MagicDroidX Nukkit Project
 */
public class AddEntityPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ADD_ENTITY_PACKET;

    public final Object[][] links = new Object[0][3];

    public long entityUniqueId;

    public long entityRuntimeId;

    public int type;

    public float x;

    public float y;

    public float z;

    public float speedX = 0f;

    public float speedY = 0f;

    public float speedZ = 0f;

    public float yaw;

    public float pitch;

    public EntityMetadata metadata = new EntityMetadata();

    public Attribute[] attributes = new Attribute[0];

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
        this.putVarLong(this.entityUniqueId);
        this.putVarLong(this.entityRuntimeId);
        this.putUnsignedVarInt(this.type);
        this.putVector3f(this.x, this.y, this.z);
        this.putVector3f(this.speedX, this.speedY, this.speedZ);
        this.putLFloat(this.pitch);
        this.putLFloat(this.yaw);
        this.putAttributeList(this.attributes);
        this.put(Binary.writeMetadata(this.metadata));
        this.putUnsignedVarInt(this.links.length);
        for (Object[] link : this.links) {
            this.putVarLong((long) link[0]);
            this.putVarLong((long) link[1]);
            this.putByte((byte) link[2]);
        }
    }

}
