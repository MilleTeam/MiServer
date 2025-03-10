package com.github.mille.team.block;

import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemDoorAcacia;

public class BlockDoorAcacia extends BlockDoorWood {

    public BlockDoorAcacia() {
        this(0);
    }

    public BlockDoorAcacia(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Acacia Door Block";
    }

    @Override
    public int getId() {
        return ACACIA_DOOR_BLOCK;
    }

    @Override
    public Item toItem() {
        return new ItemDoorAcacia();
    }

}
