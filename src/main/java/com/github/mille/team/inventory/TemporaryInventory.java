package com.github.mille.team.inventory;

abstract public class TemporaryInventory extends ContainerInventory {

    public TemporaryInventory(
        InventoryHolder holder,
        InventoryType type
    ) {
        super(holder, type);
    }

    abstract public Object getResultSlotIndex();

}
