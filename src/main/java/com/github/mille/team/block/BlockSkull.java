package com.github.mille.team.block;

/**
 * author: Justin
 */

import com.github.mille.team.Player;
import com.github.mille.team.block.entity.BlockEntity;
import com.github.mille.team.block.entity.BlockEntitySkull;
import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemSkull;
import com.github.mille.team.item.ItemTool;
import com.github.mille.team.math.BlockFace;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.nbt.tag.Tag;


public class BlockSkull extends BlockTransparent {

    public BlockSkull() {
        this(0);
    }

    public BlockSkull(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SKULL_BLOCK;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public String getName() {
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        int itemMeta = 0;
        if (blockEntity != null) itemMeta = blockEntity.namedTag.getByte("SkullType");
        return ItemSkull.getItemSkullName(itemMeta);
    }

    @Override
    public boolean place(
        Item item,
        Block block,
        Block target,
        BlockFace face,
        double fx,
        double fy,
        double fz
    ) {
        return this.place(item, block, target, face, fx, fy, fz, null);
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
        switch (face) {
            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
            case UP:
                this.meta = face.getIndex();
                break;
            case DOWN:
            default:
                return false;
        }
        this.getLevel().setBlock(block, this, true, true);

        CompoundTag nbt = new CompoundTag()
            .putString("id", BlockEntity.SKULL)
            .putByte("SkullType", item.getDamage())
            .putInt("x", block.getFloorX())
            .putInt("y", block.getFloorY())
            .putInt("z", block.getFloorZ())
            .putByte("Rot", (int) Math.floor((player.yaw * 16 / 360) + 0.5) & 0x0f);
        if (item.hasCustomBlockData()) {
            for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                nbt.put(aTag.getName(), aTag);
            }
        }
        new BlockEntitySkull(getLevel().getChunk((int) block.x >> 4, (int) block.z >> 4), nbt);

        // TODO: 2016/2/3 SPAWN WITHER

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        int dropMeta = 0;
        if (blockEntity != null) dropMeta = blockEntity.namedTag.getByte("SkullType");
        return new Item[]{
            new ItemSkull(dropMeta)
        };
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

}