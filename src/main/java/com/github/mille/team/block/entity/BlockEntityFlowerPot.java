package com.github.mille.team.block.entity;

import com.github.mille.team.block.Block;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.nbt.tag.CompoundTag;

/**
 * Created by Snake1999 on 2016/2/4. Package com.github.mille.team.blockentity in project Nukkit.
 */
public class BlockEntityFlowerPot extends BlockEntitySpawnable {

    public BlockEntityFlowerPot(
        FullChunk chunk,
        CompoundTag nbt
    ) {
        super(chunk, nbt);
        if (!nbt.contains("item")) {
            nbt.putShort("item", 0);
        }

        if (!nbt.contains("data")) {
            if (nbt.contains("mData")) {
                nbt.putInt("data", nbt.getInt("mData"));
                nbt.remove("mData");
            } else {
                nbt.putInt("data", 0);
            }
        }

        this.namedTag = nbt;
    }

    @Override
    public boolean isBlockEntityValid() {
        int blockID = getBlock().getId();
        return blockID == Block.FLOWER_POT_BLOCK;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
            .putString("id", BlockEntity.FLOWER_POT)
            .putInt("x", (int) this.x)
            .putInt("y", (int) this.y)
            .putInt("z", (int) this.z)
            .putShort("item", this.namedTag.getShort("item"))
            .putInt("mData", this.namedTag.getInt("data"));
    }

}
