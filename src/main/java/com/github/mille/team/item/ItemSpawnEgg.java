package com.github.mille.team.item;

import com.github.mille.team.Player;
import com.github.mille.team.block.Block;
import com.github.mille.team.entity.Entity;
import com.github.mille.team.level.Level;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.math.BlockFace;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.nbt.tag.DoubleTag;
import com.github.mille.team.nbt.tag.FloatTag;
import com.github.mille.team.nbt.tag.ListTag;

import java.util.Random;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ItemSpawnEgg extends Item {

    public ItemSpawnEgg() {
        this(0, 1);
    }

    public ItemSpawnEgg(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEgg(
        Integer meta,
        int count
    ) {
        super(SPAWN_EGG, meta, count, "Spawn EntityEgg");
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
        FullChunk chunk = level.getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4);

        if (chunk == null) {
            return false;
        }

        CompoundTag nbt = new CompoundTag()
            .putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("", block.getX() + 0.5))
                .add(new DoubleTag("", block.getY()))
                .add(new DoubleTag("", block.getZ() + 0.5)))
            .putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("", 0))
                .add(new DoubleTag("", 0))
                .add(new DoubleTag("", 0)))
            .putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("", new Random().nextFloat() * 360))
                .add(new FloatTag("", 0)));

        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName());
        }

        Entity entity = Entity.createEntity(this.meta, chunk, nbt);

        if (entity != null) {
            if (player.isSurvival()) {
                Item item = player.getInventory().getItemInHand();
                item.setCount(item.getCount() - 1);
                player.getInventory().setItemInHand(item);
            }
            entity.spawnToAll();
            return true;
        }

        return false;
    }

}
