package com.github.mille.team.event.entity;

import com.github.mille.team.entity.Entity;
import com.github.mille.team.entity.EntityCreature;
import com.github.mille.team.entity.EntityHuman;
import com.github.mille.team.entity.item.EntityItem;
import com.github.mille.team.entity.item.EntityVehicle;
import com.github.mille.team.entity.projectile.EntityProjectile;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.level.Position;

/**
 * author: MagicDroidX Nukkit Project
 */
public class EntitySpawnEvent extends EntityEvent {

    private static final HandlerList handlers = new HandlerList();

    private final int entityType;

    public EntitySpawnEvent(Entity entity) {
        this.entity = entity;
        this.entityType = entity.getNetworkId();
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Position getPosition() {
        return this.entity.getPosition();
    }

    public int getType() {
        return this.entityType;
    }

    public boolean isCreature() {
        return this.entity instanceof EntityCreature;
    }

    public boolean isHuman() {
        return this.entity instanceof EntityHuman;
    }

    public boolean isProjectile() {
        return this.entity instanceof EntityProjectile;
    }

    public boolean isVehicle() {
        return this.entity instanceof EntityVehicle;
    }

    public boolean isItem() {
        return this.entity instanceof EntityItem;
    }

}
