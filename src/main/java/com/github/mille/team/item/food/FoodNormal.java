package com.github.mille.team.item.food;

/**
 * Created by Snake1999 on 2016/1/13. Package com.github.mille.team.item.food in project nukkit.
 */
public class FoodNormal extends Food {

    public FoodNormal(
        int restoreFood,
        float restoreSaturation
    ) {
        this.setRestoreFood(restoreFood);
        this.setRestoreSaturation(restoreSaturation);
    }

}
