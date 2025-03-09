package com.github.mille.team.level.particle;

import com.github.mille.team.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr. Package com.github.mille.team.level.particle in project Nukkit .
 */
public class RedstoneParticle extends GenericParticle {

    public RedstoneParticle(Vector3 pos) {
        this(pos, 1);
    }

    public RedstoneParticle(
        Vector3 pos,
        int lifetime
    ) {
        super(pos, Particle.TYPE_REDSTONE, lifetime);
    }

}
