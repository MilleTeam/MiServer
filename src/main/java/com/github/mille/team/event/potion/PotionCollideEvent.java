package com.github.mille.team.event.potion;

import com.github.mille.team.entity.item.EntityPotion;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.potion.Potion;

/**
 * Created by Snake1999 on 2016/1/12. Package com.github.mille.team.event.potion in project nukkit
 */
public class PotionCollideEvent extends PotionEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final EntityPotion thrownPotion;

    public PotionCollideEvent(
        Potion potion,
        EntityPotion thrownPotion
    ) {
        super(potion);
        this.thrownPotion = thrownPotion;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EntityPotion getThrownPotion() {
        return thrownPotion;
    }

}
