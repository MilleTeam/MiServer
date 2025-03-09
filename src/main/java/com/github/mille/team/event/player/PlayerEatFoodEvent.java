package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.item.food.Food;

/**
 * Created by Snake1999 on 2016/1/14. Package com.github.mille.team.event.player in project nukkit.
 */
public class PlayerEatFoodEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Food food;

    public PlayerEatFoodEvent(
        Player player,
        Food food
    ) {
        this.player = player;
        this.food = food;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

}
