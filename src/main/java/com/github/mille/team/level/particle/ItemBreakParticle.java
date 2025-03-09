package com.github.mille.team.level.particle;

import com.github.mille.team.item.Item;
import com.github.mille.team.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr. Package com.github.mille.team.level.particle in project Nukkit .
 */
public class ItemBreakParticle extends GenericParticle {

    public ItemBreakParticle(
        Vector3 pos,
        Item item
    ) {
        super(pos, Particle.TYPE_ITEM_BREAK, (item.getId() << 16) | item.getDamage());
    }

}
