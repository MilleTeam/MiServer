package com.github.mille.team.event.entity;

import com.github.mille.team.block.Block;
import com.github.mille.team.entity.Entity;

/**
 * author: MagicDroidX Nukkit Project
 */
public class EntityDamageByBlockEvent extends EntityDamageEvent {

    private final Block damager;

    public EntityDamageByBlockEvent(
        Block damager,
        Entity entity,
        DamageCause cause,
        float damage
    ) {
        super(entity, cause, damage);
        this.damager = damager;
    }

    public Block getDamager() {
        return damager;
    }

}
