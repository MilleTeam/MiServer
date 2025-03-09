package com.github.mille.team.entity;

import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.nbt.tag.CompoundTag;

/**
 * @author Adam Matthew
 */
public abstract class EntityInteractable extends Entity {

    public EntityInteractable(
        FullChunk chunk,
        CompoundTag nbt
    ) {
        super(chunk, nbt);
    }

    // Todo: Passive entity?? i18n and boat leaving text
    public abstract String getInteractButtonText();

    public abstract boolean canDoInteraction();

}
