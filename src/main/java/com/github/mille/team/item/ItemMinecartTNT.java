package com.github.mille.team.item;

import com.github.mille.team.Player;
import com.github.mille.team.block.Block;
import com.github.mille.team.block.BlockRail;
import com.github.mille.team.entity.item.EntityMinecartTNT;
import com.github.mille.team.level.Level;
import com.github.mille.team.math.BlockFace;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.nbt.tag.DoubleTag;
import com.github.mille.team.nbt.tag.FloatTag;
import com.github.mille.team.nbt.tag.ListTag;
import com.github.mille.team.utils.Rail;

public class ItemMinecartTNT extends Item {

    public ItemMinecartTNT() {
        this(0, 1);
    }

    public ItemMinecartTNT(Integer meta) {
        this(meta, 1);
    }

    public ItemMinecartTNT(
        Integer meta,
        int count
    ) {
        super(MINECART_WITH_TNT, meta, count, "Minecart with TNT");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(
        Level level,
        Player player,
        Block block,
        Block target,
        BlockFace face,
        double fx,
        double fy,
        double fz
    ) {
        if (Rail.isRailBlock(target)) {
            Rail.Orientation type = ((BlockRail) target).getOrientation();
            double adjacent = 0.0D;
            if (type.isAscending()) {
                adjacent = 0.5D;
            }
            EntityMinecartTNT minecart = new EntityMinecartTNT(
                level.getChunk(target.getFloorX() >> 4, target.getFloorZ() >> 4), new CompoundTag("")
                .putList(new ListTag<>("Pos")
                    .add(new DoubleTag("", target.getX() + 0.5))
                    .add(new DoubleTag("", target.getY() + 0.0625D + adjacent))
                    .add(new DoubleTag("", target.getZ() + 0.5)))
                .putList(new ListTag<>("Motion")
                    .add(new DoubleTag("", 0))
                    .add(new DoubleTag("", 0))
                    .add(new DoubleTag("", 0)))
                .putList(new ListTag<>("Rotation")
                    .add(new FloatTag("", 0))
                    .add(new FloatTag("", 0)))
            );
            minecart.spawnToAll();
            count -= 1;
            return true;
        }
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

}
