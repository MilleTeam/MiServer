package com.github.mille.team.block;

import com.github.mille.team.Player;
import com.github.mille.team.block.entity.BlockEntity;
import com.github.mille.team.block.entity.BlockEntityBeacon;
import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemTool;
import com.github.mille.team.math.BlockFace;
import com.github.mille.team.nbt.tag.CompoundTag;

/**
 * author: Angelic47 Nukkit Project
 */
public class BlockBeacon extends BlockTransparent {

    public BlockBeacon() {
        this(0);
    }

    public BlockBeacon(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BEACON;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Beacon";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(
        Item item,
        Player player
    ) {
        // TODO handle GUI
        //Server.getInstance().getLogger().info("BlockBeacon.onActivate called");
        return true;
    }

    @Override
    public boolean place(
        Item item,
        Block block,
        Block target,
        BlockFace face,
        double fx,
        double fy,
        double fz,
        Player player
    ) {
        boolean blockSuccess = super.place(item, block, target, face, fx, fy, fz, player);

        if (blockSuccess) {
            CompoundTag nbt = new CompoundTag("")
                .putString("id", BlockEntity.BEACON)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);
            new BlockEntityBeacon(this.level.getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);
        }

        return blockSuccess;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

}
