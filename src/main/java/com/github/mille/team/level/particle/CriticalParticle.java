package com.github.mille.team.level.particle;

import com.github.mille.team.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr. Package com.github.mille.team.level.particle in project Nukkit .
 */
public class CriticalParticle extends GenericParticle {

    public CriticalParticle(Vector3 pos) {
        this(pos, 2);
    }

    public CriticalParticle(
        Vector3 pos,
        int scale
    ) {
        super(pos, Particle.TYPE_CRITICAL, scale);
    }

}
