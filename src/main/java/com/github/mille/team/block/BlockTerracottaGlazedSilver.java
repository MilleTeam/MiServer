package com.github.mille.team.block;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazedSilver extends BlockTerracottaGlazed {

    public BlockTerracottaGlazedSilver() {
        this(0);
    }

    public BlockTerracottaGlazedSilver(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SILVER_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Light Gray Glazed Terracotta";
    }

}
