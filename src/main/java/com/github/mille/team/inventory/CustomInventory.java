package com.github.mille.team.inventory;

import com.github.mille.team.item.Item;

import java.util.Map;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class CustomInventory extends ContainerInventory {

    public CustomInventory(
        InventoryHolder holder,
        InventoryType type
    ) {
        super(holder, type);
    }

    public CustomInventory(
        InventoryHolder holder,
        InventoryType type,
        Map<Integer, Item> items
    ) {
        super(holder, type, items);
    }

    public CustomInventory(
        InventoryHolder holder,
        InventoryType type,
        Map<Integer, Item> items,
        Integer overrideSize
    ) {
        super(holder, type, items, overrideSize);
    }

    public CustomInventory(
        InventoryHolder holder,
        InventoryType type,
        Map<Integer, Item> items,
        Integer overrideSize,
        String overrideTitle
    ) {
        super(holder, type, items, overrideSize, overrideTitle);
    }

}
