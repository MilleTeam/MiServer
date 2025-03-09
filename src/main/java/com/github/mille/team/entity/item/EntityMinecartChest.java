package com.github.mille.team.entity.item;

import com.github.mille.team.block.BlockChest;
import com.github.mille.team.item.ItemMinecartChest;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.utils.MinecartType;

/**
 * Created by Snake1999 on 2016/1/30. Package com.github.mille.team.entity.item in project Nukkit.
 */
public class EntityMinecartChest extends EntityMinecartAbstract {

    public static final int NETWORK_ID = 98;

    public EntityMinecartChest(
        FullChunk chunk,
        CompoundTag nbt
    ) {
        super(chunk, nbt);
        super.setDisplayBlock(new BlockChest());
    }

    // TODO: 2016/1/30 inventory

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(1);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void dropItem() {
        level.dropItem(this, new ItemMinecartChest());
    }


}
