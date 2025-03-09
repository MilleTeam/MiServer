package com.github.mille.team.block;

import com.github.mille.team.Player;
import com.github.mille.team.block.entity.BlockEntity;
import com.github.mille.team.block.entity.BlockEntityShulkerBox;
import com.github.mille.team.inventory.ShulkerBoxInventory;
import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemShulkerBox;
import com.github.mille.team.math.BlockFace;
import com.github.mille.team.nbt.NBTIO;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.nbt.tag.ListTag;
import com.github.mille.team.nbt.tag.StringTag;
import com.github.mille.team.nbt.tag.Tag;
import com.github.mille.team.utils.BlockColor;
import com.github.mille.team.utils.DyeColor;

import java.util.Map;

public class BlockShulkerBox extends BlockTransparent {

    public BlockShulkerBox() {
        this(0);
    }

    public BlockShulkerBox(int meta) {
        super(meta);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getId() {
        return SHULKER_BOX;
    }

    @Override
    public String getName() {
        return this.getDyeColor().getName() + " Shulker Box";
    }

    @Override
    public double getHardness() {
        return 6;
    }

    @Override
    public double getResistance() {
        return 30;
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
        this.getLevel().setBlock(block, this, true, true);
        CompoundTag nbt = new CompoundTag("")
            .putString("id", BlockEntity.SHULKER_BOX)
            .putByte("facing", face.getIndex())
            .putInt("x", (int) this.x)
            .putInt("y", (int) this.y)
            .putInt("z", (int) this.z);

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        new BlockEntityShulkerBox(this.getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);
        return true;
    }

    @Override
    public boolean onActivate(
        Item item,
        Player player
    ) {
        if (player != null) {
            Block top = up();
            if (!top.isTransparent()) {
                return true;
            }

            BlockEntity t = this.getLevel().getBlockEntity(this);
            BlockEntityShulkerBox shulkerBox;
            if (t instanceof BlockEntityShulkerBox) {
                shulkerBox = (BlockEntityShulkerBox) t;
            } else {
                CompoundTag nbt = new CompoundTag("")
                    .putList(new ListTag<>("Items"))
                    .putString("id", BlockEntity.SHULKER_BOX)
                    .putInt("x", (int) this.x)
                    .putInt("y", (int) this.y)
                    .putInt("z", (int) this.z);
                shulkerBox = new BlockEntityShulkerBox(this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
            }

            if (shulkerBox.namedTag.contains("Lock") && shulkerBox.namedTag.get("Lock") instanceof StringTag) {
                if (!shulkerBox.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return true;
                }
            }

            player.addWindow(shulkerBox.getInventory());
        }

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public Item toItem() {
        Item item = new ItemShulkerBox(this.meta, 1);
        BlockEntityShulkerBox t = (BlockEntityShulkerBox) this.getLevel().getBlockEntity(this);
        if (t.hasName()) {
            item.setCustomName(t.getName());
        }
        item.setCustomBlockData(t.getCleanedNBT());

        if (t != null) {
            ShulkerBoxInventory i = t.getInventory();

            if (!i.isEmpty()) {
                CompoundTag nbt = item.getNamedTag();
                if (nbt == null)
                    nbt = new CompoundTag("");

                ListTag<CompoundTag> items = new ListTag<>();

                for (int it = 0; it < i.getSize(); it++) {
                    if (i.getItem(it).getId() != Item.AIR) {
                        CompoundTag d = NBTIO.putItemHelper(i.getItem(it), it);
                        items.add(d);
                    }
                }

                nbt.put("Items", items);

                item.setCompoundTag(nbt);
            }

            if (t.hasName()) {
                item.setCustomName(t.getName());
            }
        }

        return item;
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByDyeData(this.meta);
    }

    @Override
    public BlockColor getColor() {
        return this.getDyeColor().getColor();
    }

}
