package com.github.mille.team.inventory;

/**
 * author: MagicDroidX Nukkit Project
 */
public class CraftingInventory extends BaseInventory {

    private final Inventory resultInventory;

    public CraftingInventory(
        InventoryHolder holder,
        Inventory resultInventory,
        InventoryType type
    ) {
        super(holder, type);
        if (!type.getDefaultTitle().equals("Crafting")) {
            throw new IllegalStateException("Invalid Inventory type, expected CRAFTING or WORKBENCH");
        }
        this.resultInventory = resultInventory;
    }

    public Inventory getResultInventory() {
        return resultInventory;
    }

    @Override
    public int getSize() {
        return this.getResultInventory().getSize() + super.getSize();
    }

}
