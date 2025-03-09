package com.github.mille.team.inventory;

public class FloatingInventory extends BaseInventory {

    public FloatingInventory(InventoryHolder holder) {
        super(holder, InventoryType.PLAYER_FLOATING);
    }

}
