package com.github.mille.team.level.particle;

import com.github.mille.team.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr. Package com.github.mille.team.level.particle in project Nukkit .
 */
public class HeartParticle extends GenericParticle {

    public HeartParticle(Vector3 pos) {
        this(pos, 0);
    }

    public HeartParticle(
        Vector3 pos,
        int scale
    ) {
        super(pos, Particle.TYPE_HEART, scale);
    }

}
