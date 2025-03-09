package com.github.mille.team.level.particle;

import com.github.mille.team.block.Block;
import com.github.mille.team.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr. Package com.github.mille.team.level.particle in project Nukkit .
 */
public class TerrainParticle extends GenericParticle {

    public TerrainParticle(
        Vector3 pos,
        Block block
    ) {
        super(pos, Particle.TYPE_TERRAIN, (block.getDamage() << 8) | block.getId());
    }

}
