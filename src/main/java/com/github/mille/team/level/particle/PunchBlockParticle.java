package com.github.mille.team.level.particle;

import com.github.mille.team.block.Block;
import com.github.mille.team.math.BlockFace;
import com.github.mille.team.math.Vector3;
import com.github.mille.team.network.protocol.DataPacket;
import com.github.mille.team.network.protocol.LevelEventPacket;

public class PunchBlockParticle extends Particle {

    protected final int data;

    public PunchBlockParticle(
        Vector3 pos,
        Block block,
        BlockFace face
    ) {
        this(pos, block.getId(), block.getDamage(), face);
    }

    public PunchBlockParticle(
        Vector3 pos,
        int blockId,
        int blockDamage,
        BlockFace face
    ) {
        super(pos.x, pos.y, pos.z);
        this.data = blockId | (blockDamage << 8) | (face.getIndex() << 16);
    }

    @Override
    public DataPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = LevelEventPacket.EVENT_PARTICLE_PUNCH_BLOCK;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.data = this.data;

        return new DataPacket[]{pk};
    }

}
