package com.github.mille.team.level.particle;

import com.github.mille.team.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr. Package com.github.mille.team.level.particle in project Nukkit .
 */
public class SmokeParticle extends GenericParticle {

    public SmokeParticle(Vector3 pos) {
        this(pos, 0);
    }

    public SmokeParticle(
        Vector3 pos,
        int scale
    ) {
        super(pos, Particle.TYPE_SMOKE, scale);
    }

}
