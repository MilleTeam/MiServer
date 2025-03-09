package com.github.mille.team.level.particle;

import com.github.mille.team.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr. Package com.github.mille.team.level.particle in project Nukkit .
 */
public class InkParticle extends GenericParticle {

    public InkParticle(Vector3 pos) {
        this(pos, 0);
    }

    public InkParticle(
        Vector3 pos,
        int scale
    ) {
        super(pos, Particle.TYPE_INK, scale);
    }

}
