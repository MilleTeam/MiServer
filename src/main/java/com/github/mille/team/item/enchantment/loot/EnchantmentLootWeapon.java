package com.github.mille.team.item.enchantment.loot;

import com.github.mille.team.item.enchantment.Enchantment;
import com.github.mille.team.item.enchantment.EnchantmentType;

/**
 * author: MagicDroidX Nukkit Project
 */
public class EnchantmentLootWeapon extends EnchantmentLoot {

    public EnchantmentLootWeapon() {
        super(Enchantment.ID_LOOTING, "lootBonus", 2, EnchantmentType.SWORD);
    }

}
