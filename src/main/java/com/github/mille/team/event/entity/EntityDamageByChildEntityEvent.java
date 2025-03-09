package com.github.mille.team.event.entity;

import com.github.mille.team.entity.Entity;

/**
 * author: MagicDroidX Nukkit Project
 */
public class EntityDamageByChildEntityEvent extends EntityDamageByEntityEvent {

    private final Entity childEntity;

    public EntityDamageByChildEntityEvent(
        Entity damager,
        Entity childEntity,
        Entity entity,
        DamageCause cause,
        float damage
    ) {
        super(damager, entity, cause, damage);
        this.childEntity = childEntity;
    }

    public Entity getChild() {
        return childEntity;
    }

}
