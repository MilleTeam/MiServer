package com.github.mille.team.inventory;

import com.github.mille.team.level.Position;

/**
 * author: MagicDroidX Nukkit Project
 */
public class FakeBlockMenu extends Position implements InventoryHolder {

    private final Inventory inventory;

    public FakeBlockMenu(
        Inventory inventory,
        Position pos
    ) {
        super(pos.x, pos.y, pos.z, pos.level);
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

}
