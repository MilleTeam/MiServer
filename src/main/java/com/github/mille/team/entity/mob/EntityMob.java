package com.github.mille.team.entity.mob;

import com.github.mille.team.entity.EntityCreature;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class EntityMob extends EntityCreature {

    public EntityMob(
        FullChunk chunk,
        CompoundTag nbt
    ) {
        super(chunk, nbt);
    }

}
