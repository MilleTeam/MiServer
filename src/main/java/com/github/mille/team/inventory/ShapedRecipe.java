package com.github.mille.team.inventory;

import com.github.mille.team.Server;
import com.github.mille.team.block.BlockIds;
import com.github.mille.team.item.Item;

import java.util.*;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ShapedRecipe implements Recipe {

    private final Map<Character, String> shapes = new HashMap<>();

    private final Map<Integer, Map<Integer, Item>> ingredients = new HashMap<>();

    private final Map<Character, List<Entry>> shapeItems = new HashMap<>();

    private final Item output;

    private UUID uuid = null;

    public ShapedRecipe(
        Item result,
        int height,
        int width
    ) {
        for (int y = 0; y < height; y++) {
            if (width == 0 || width > 3) {
                throw new IllegalStateException("Crafting rows should be 1, 2, 3 characters, not " + width);
            }

            this.ingredients.put(y, new HashMap<Integer, Item>() {

                {
                    for (int i = 0; i < width; i++) {
                        put(i, null);
                    }
                }
            });
        }

        this.output = result.clone();
    }

    public int getWidth() {
        return this.ingredients.get(0).size();
    }

    public int getHeight() {
        return this.ingredients.size();
    }

    @Override
    public Item getResult() {
        return this.output;
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public void setId(UUID id) {
        if (this.uuid != null) {
            throw new IllegalStateException("Id is already set");
        }
        this.uuid = id;
    }

    public ShapedRecipe addIngredient(
        int x,
        int y,
        Item item
    ) {
        this.ingredients.get(y).put(x, item.clone());
        return this;
    }

    public ShapedRecipe setIngredient(
        String key,
        Item item
    ) {
        return this.setIngredient(key.charAt(0), item);
    }

    public ShapedRecipe setIngredient(
        char key,
        Item item
    ) {
        if (!this.shapes.containsKey(key)) {
            throw new RuntimeException("Symbol does not appear in the shape: " + key);
        }

        item.setCount(1);
        this.fixRecipe(key, item);

        return this;
    }

    protected void fixRecipe(
        char key,
        Item item
    ) {
        for (Entry entry : this.shapeItems.get(key)) {
            this.ingredients.get(entry.y).put(entry.x, item.clone());
        }
    }

    public Map<Integer, Map<Integer, Item>> getIngredientMap() {
        Map<Integer, Map<Integer, Item>> ingredients = new HashMap<>();
        for (int y : this.ingredients.keySet()) {
            Map<Integer, Item> row = this.ingredients.get(y);

            ingredients.put(y, new HashMap<>());

            for (int x : row.keySet()) {
                Item ingredient = row.get(x);

                if (ingredient != null) {
                    ingredients.get(y).put(x, ingredient.clone());
                } else {
                    ingredients.get(y).put(x, Item.get(Item.AIR));
                }
            }

        }

        return ingredients;
    }

    public List<Item> getIngredientList() {
        List<Item> ingredients = new ArrayList<>();
        for (int y : this.ingredients.keySet()) {
            Map<Integer, Item> row = this.ingredients.get(y);
            for (int x : row.keySet()) {
                Item ingredient = row.get(x);
                if (ingredient != null) {
                    if (ingredient.getId() != BlockIds.AIR) {
                        ingredients.add(ingredient.clone());
                    }
                }
            }
        }
        return ingredients;
    }


    public Item getIngredient(
        int x,
        int y
    ) {
        if (this.ingredients.containsKey(y)) {
            if (this.ingredients.get(y).containsKey(x)) {
                return this.ingredients.get(y).get(x) != null ? this.ingredients.get(y).get(x) : Item.get(Item.AIR);
            }
        }

        return Item.get(Item.AIR);
    }

    public Map<Character, String> getShape() {
        return shapes;
    }

    @Override
    public void registerToCraftingManager() {
        Server.getInstance().getCraftingManager().registerShapedRecipe(this);
    }

    public static class Entry {

        public final int x;

        public final int y;

        public Entry(
            int x,
            int y
        ) {
            this.x = x;
            this.y = y;
        }

    }

}
