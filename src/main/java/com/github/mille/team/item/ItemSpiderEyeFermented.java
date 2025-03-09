package com.github.mille.team.item;

public class ItemSpiderEyeFermented extends Item {

    public ItemSpiderEyeFermented() {
        this(0, 1);
    }

    public ItemSpiderEyeFermented(Integer meta) {
        this(meta, 1);
    }

    public ItemSpiderEyeFermented(
        Integer meta,
        int count
    ) {
        super(FERMENTED_SPIDER_EYE, meta, count, "Fermented Spider Eye");
    }

}
