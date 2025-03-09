package com.github.mille.team.item.enchantment.loot;

import com.github.mille.team.item.enchantment.Enchantment;
import com.github.mille.team.item.enchantment.EnchantmentType;

/**
 * author: MagicDroidX Nukkit Project
 */
public class EnchantmentLootDigging extends EnchantmentLoot {

    public EnchantmentLootDigging() {
        super(Enchantment.ID_FORTUNE_DIGGING, "lootBonusDigger", 2, EnchantmentType.DIGGER);
    }

}
