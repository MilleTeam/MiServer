package com.github.mille.team.entity.passive;

import com.github.mille.team.entity.Entity;
import com.github.mille.team.entity.EntityAgeable;
import com.github.mille.team.entity.EntityCreature;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class EntityWaterAnimal extends EntityCreature implements EntityAgeable {

    public EntityWaterAnimal(
        FullChunk chunk,
        CompoundTag nbt
    ) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBaby() {
        return this.getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY);
    }

}
