package com.github.mille.team.metadata;

import com.github.mille.team.entity.Entity;

/**
 * author: MagicDroidX Nukkit Project
 */
public class EntityMetadataStore extends MetadataStore {

    @Override
    protected String disambiguate(
        Metadatable entity,
        String metadataKey
    ) {
        if (!(entity instanceof Entity)) {
            throw new IllegalArgumentException("Argument must be an Entity instance");
        }
        return ((Entity) entity).getId() + ":" + metadataKey;
    }

}
