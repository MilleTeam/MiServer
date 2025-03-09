package com.github.mille.team.event.potion;

import com.github.mille.team.event.Event;
import com.github.mille.team.potion.Potion;

/**
 * Created by Snake1999 on 2016/1/12. Package com.github.mille.team.event.potion in project nukkit
 */
public abstract class PotionEvent extends Event {

    private Potion potion;

    public PotionEvent(Potion potion) {
        this.potion = potion;
    }

    public Potion getPotion() {
        return potion;
    }

    public void setPotion(Potion potion) {
        this.potion = potion;
    }

}
