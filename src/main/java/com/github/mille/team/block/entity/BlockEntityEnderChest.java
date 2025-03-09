package com.github.mille.team.block.entity;

import com.github.mille.team.block.Block;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.nbt.tag.CompoundTag;

public class BlockEntityEnderChest extends BlockEntitySpawnable {

    public BlockEntityEnderChest(
        FullChunk chunk,
        CompoundTag nbt
    ) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == Block.ENDER_CHEST;
    }

    @Override
    public String getName() {
        return "EnderChest";
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
            .putString("id", BlockEntity.ENDER_CHEST)
            .putInt("x", (int) this.x)
            .putInt("y", (int) this.y)
            .putInt("z", (int) this.z);
    }

}
