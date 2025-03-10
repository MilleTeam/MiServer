package com.github.mille.team.item;

import com.github.mille.team.Player;
import com.github.mille.team.block.Block;
import com.github.mille.team.entity.item.EntityBoat;
import com.github.mille.team.level.Level;
import com.github.mille.team.math.BlockFace;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.nbt.tag.DoubleTag;
import com.github.mille.team.nbt.tag.FloatTag;
import com.github.mille.team.nbt.tag.ListTag;

/**
 * Created by yescallop on 2016/2/13.
 */
public class ItemBoat extends Item {

    public ItemBoat() {
        this(0, 1);
    }

    public ItemBoat(Integer meta) {
        this(meta, 1);
    }

    public ItemBoat(
        Integer meta,
        int count
    ) {
        super(BOAT, meta, count, "Boat");
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
        if (face != BlockFace.UP) return false;
        EntityBoat boat = new EntityBoat(
            level.getChunk(block.getFloorX() >> 4, block.getFloorZ() >> 4), new CompoundTag("")
            .putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("", block.getX() + 0.5))
                .add(new DoubleTag("", block.getY() - 0.0625))
                .add(new DoubleTag("", block.getZ() + 0.5)))
            .putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("", 0))
                .add(new DoubleTag("", 0))
                .add(new DoubleTag("", 0)))
            .putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("", (float) ((player.yaw + 90f) % 360)))
                .add(new FloatTag("", 0)))
            .putByte("woodID", this.getDamage())
        );

        if (player.isSurvival()) {
            Item item = player.getInventory().getItemInHand();
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInHand(item);
        }

        boat.spawnToAll();
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

}
