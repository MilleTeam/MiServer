package com.github.mille.team.block.entity;

import com.github.mille.team.block.BlockRedstoneComparator;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.nbt.tag.CompoundTag;

/**
 * @author CreeperFace
 */
public class BlockEntityComparator extends BlockEntity {

    private int outputSignal;

    public BlockEntityComparator(
        FullChunk chunk,
        CompoundTag nbt
    ) {
        super(chunk, nbt);

        if (!nbt.contains("OutputSignal")) {
            nbt.putInt("OutputSignal", 0);
        }

        this.outputSignal = nbt.getInt("OutputSignal");
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getLevelBlock() instanceof BlockRedstoneComparator;
    }

    public int getOutputSignal() {
        return outputSignal;
    }

    public void setOutputSignal(int outputSignal) {
        this.outputSignal = outputSignal;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt("OutputSignal", this.outputSignal);
    }

}
