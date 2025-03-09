package com.github.mille.team.inventory;

import com.github.mille.team.item.Item;

import java.util.UUID;

/**
 * author: MagicDroidX Nukkit Project
 */
public interface Recipe {

    Item getResult();

    void registerToCraftingManager();

    UUID getId();

    void setId(UUID id);

}
