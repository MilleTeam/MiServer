package com.github.mille.team.event.potion;

import com.github.mille.team.entity.Entity;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.potion.Effect;
import com.github.mille.team.potion.Potion;

/**
 * Created by Snake1999 on 2016/1/12. Package com.github.mille.team.event.potion in project nukkit
 */
public class PotionApplyEvent extends PotionEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Entity entity;

    private Effect applyEffect;

    public PotionApplyEvent(
        Potion potion,
        Effect applyEffect,
        Entity entity
    ) {
        super(potion);
        this.applyEffect = applyEffect;
        this.entity = entity;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Entity getEntity() {
        return entity;
    }

    public Effect getApplyEffect() {
        return applyEffect;
    }

    public void setApplyEffect(Effect applyEffect) {
        this.applyEffect = applyEffect;
    }

}
