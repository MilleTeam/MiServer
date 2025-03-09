package com.github.mille.team.item.enchantment.bow;

import com.github.mille.team.item.enchantment.Enchantment;
import com.github.mille.team.item.enchantment.EnchantmentType;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class EnchantmentBow extends Enchantment {

    protected EnchantmentBow(
        int id,
        String name,
        int weight
    ) {
        super(id, name, weight, EnchantmentType.BOW);
    }

}
