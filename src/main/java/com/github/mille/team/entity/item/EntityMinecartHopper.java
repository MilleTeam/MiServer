package com.github.mille.team.entity.item;

import com.github.mille.team.block.BlockHopper;
import com.github.mille.team.item.ItemMinecartHopper;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.utils.MinecartType;

public class EntityMinecartHopper extends EntityMinecartAbstract {

    public static final int NETWORK_ID = 96;

    public EntityMinecartHopper(
        FullChunk chunk,
        CompoundTag nbt
    ) {
        super(chunk, nbt);
        super.setDisplayBlock(new BlockHopper());
    }

    // TODO: 2016/12/18 inventory

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(5);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void dropItem() {
        level.dropItem(this, new ItemMinecartHopper());
    }

}
