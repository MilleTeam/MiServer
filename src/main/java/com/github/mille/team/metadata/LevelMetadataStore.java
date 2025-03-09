package com.github.mille.team.metadata;

import com.github.mille.team.level.Level;

/**
 * author: MagicDroidX Nukkit Project
 */
public class LevelMetadataStore extends MetadataStore {

    @Override
    protected String disambiguate(
        Metadatable level,
        String metadataKey
    ) {
        if (!(level instanceof Level)) {
            throw new IllegalArgumentException("Argument must be a Level instance");
        }
        return (((Level) level).getName() + ":" + metadataKey).toLowerCase();
    }

}
