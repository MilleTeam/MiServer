package com.github.mille.team.entity;

import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class EntityCreature extends EntityLiving {

    public EntityCreature(
        FullChunk chunk,
        CompoundTag nbt
    ) {
        super(chunk, nbt);
    }

}
