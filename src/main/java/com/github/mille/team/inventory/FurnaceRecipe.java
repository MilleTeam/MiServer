package com.github.mille.team.inventory;

import com.github.mille.team.Server;
import com.github.mille.team.item.Item;

import java.util.UUID;

/**
 * author: MagicDroidX Nukkit Project
 */
public class FurnaceRecipe implements Recipe {

    private final Item output;

    private UUID uuid = null;

    private Item ingredient;

    public FurnaceRecipe(
        Item result,
        Item ingredient
    ) {
        this.output = result.clone();
        this.ingredient = ingredient.clone();
    }

    public Item getInput() {
        return this.ingredient.clone();
    }

    public void setInput(Item item) {
        this.ingredient = item.clone();
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public void setId(UUID uuid) {
        if (this.uuid != null) {
            throw new IllegalStateException("Id is already set");
        }
        this.uuid = uuid;
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }

    @Override
    public void registerToCraftingManager() {
        Server.getInstance().getCraftingManager().registerFurnaceRecipe(this);
    }

}
