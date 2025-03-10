package com.github.mille.team.network.protocol;

import com.github.mille.team.item.Item;
import com.github.mille.team.math.BlockVector3;
import com.github.mille.team.math.Vector3f;

/**
 * @author Nukkit Project Team
 */
public class UseItemPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.USE_ITEM_PACKET;

    public int x;

    public int y;

    public int z;

    public int interactBlockId;

    public int face;

    public float fx;

    public float fy;

    public float fz;

    public float posX;

    public float posY;

    public float posZ;

    public int slot;

    public Item item;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        BlockVector3 v = this.getBlockCoords();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.interactBlockId = (int) this.getUnsignedVarInt();
        this.face = this.getVarInt();
        Vector3f faceVector3 = this.getVector3f();
        this.fx = faceVector3.x;
        this.fy = faceVector3.y;
        this.fz = faceVector3.z;
        Vector3f playerPos = this.getVector3f();
        this.posX = playerPos.x;
        this.posY = playerPos.y;
        this.posZ = playerPos.z;
        this.slot = this.getByte();
        this.item = this.getSlot();
    }

    @Override
    public void encode() {

    }

}
