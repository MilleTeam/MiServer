package com.github.mille.team.item;

import com.github.mille.team.Player;
import com.github.mille.team.block.Block;
import com.github.mille.team.entity.item.EntityPainting;
import com.github.mille.team.level.Level;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.math.BlockFace;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.nbt.tag.DoubleTag;
import com.github.mille.team.nbt.tag.FloatTag;
import com.github.mille.team.nbt.tag.ListTag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ItemPainting extends Item {

    public ItemPainting() {
        this(0, 1);
    }

    public ItemPainting(Integer meta) {
        this(meta, 1);
    }

    public ItemPainting(
        Integer meta,
        int count
    ) {
        super(PAINTING, 0, count, "Painting");
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

        if (!target.isTransparent() && face.getIndex() > 1 && !block.isSolid()) {
            int[] direction = {2, 0, 1, 3};
            int[] right = {4, 5, 3, 2};

            List<EntityPainting.Motive> validMotives = new ArrayList<>();
            for (EntityPainting.Motive motive : EntityPainting.motives) {
                boolean valid = true;
                for (int x = 0; x < motive.width && valid; x++) {
                    for (int z = 0; z < motive.height && valid; z++) {
                        if (target.getSide(BlockFace.fromIndex(right[face.getIndex() - 2]), x).isTransparent() ||
                            target.up(z).isTransparent() ||
                            block.getSide(BlockFace.fromIndex(right[face.getIndex() - 2]), x).isSolid() ||
                            block.up(z).isSolid()) {
                            valid = false;
                        }
                    }
                }

                if (valid) {
                    validMotives.add(motive);
                }
            }

            CompoundTag nbt = new CompoundTag()
                .putByte("Direction", direction[face.getIndex() - 2])
                .putString("Motive", validMotives.get(ThreadLocalRandom.current().nextInt(validMotives.size())).title)
                .putList(new ListTag<DoubleTag>("Pos")
                    .add(new DoubleTag("0", target.x))
                    .add(new DoubleTag("1", target.y))
                    .add(new DoubleTag("2", target.z)))
                .putList(new ListTag<DoubleTag>("Motion")
                    .add(new DoubleTag("0", 0))
                    .add(new DoubleTag("1", 0))
                    .add(new DoubleTag("2", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                    .add(new FloatTag("0", direction[face.getIndex() - 2] * 90))
                    .add(new FloatTag("1", 0)));

            EntityPainting entity = new EntityPainting(chunk, nbt);

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
