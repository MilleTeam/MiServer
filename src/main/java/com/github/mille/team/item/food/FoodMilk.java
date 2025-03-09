package com.github.mille.team.item.food;

import com.github.mille.team.Player;
import com.github.mille.team.item.ItemBucket;

/**
 * Created by Snake1999 on 2016/1/21. Package com.github.mille.team.item.food in project nukkit.
 */
public class FoodMilk extends Food {

    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);
        player.getInventory().addItem(new ItemBucket());
        player.removeAllEffects();
        return true;
    }

}
