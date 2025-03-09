package com.github.mille.team.level.particle;

import com.github.mille.team.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr. Package com.github.mille.team.level.particle in project Nukkit .
 */
public class WaterParticle extends GenericParticle {

    public WaterParticle(Vector3 pos) {
        super(pos, Particle.TYPE_WATER_WAKE);
    }

}
