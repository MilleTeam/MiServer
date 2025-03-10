package com.github.mille.team.block;


import com.github.mille.team.Player;
import com.github.mille.team.block.entity.BlockEntity;
import com.github.mille.team.block.entity.BlockEntityBrewingStand;
import com.github.mille.team.inventory.ContainerInventory;
import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemBrewingStand;
import com.github.mille.team.item.ItemTool;
import com.github.mille.team.math.BlockFace;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.nbt.tag.ListTag;
import com.github.mille.team.nbt.tag.StringTag;
import com.github.mille.team.nbt.tag.Tag;
import com.github.mille.team.utils.BlockColor;

import java.util.Map;

public class BlockBrewingStand extends BlockSolid {

    public BlockBrewingStand() {
        this(0);
    }

    public BlockBrewingStand(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Brewing Stand";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getId() {
        return BREWING_STAND_BLOCK;
    }

    @Override
    public int getLightLevel() {
        return 1;
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
        if (!block.down().isTransparent()) {
            getLevel().setBlock(block, this, true, true);

            CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<>("Items"))
                .putString("id", BlockEntity.BREWING_STAND)
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

            new BlockEntityBrewingStand(getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);

            return true;
        }
        return false;
    }

    @Override
    public boolean onActivate(
        Item item,
        Player player
    ) {
        if (player != null) {
            BlockEntity t = getLevel().getBlockEntity(this);
            BlockEntityBrewingStand brewing;
            if (t instanceof BlockEntityBrewingStand) {
                brewing = (BlockEntityBrewingStand) t;
            } else {
                CompoundTag nbt = new CompoundTag()
                    .putList(new ListTag<>("Items"))
                    .putString("id", BlockEntity.BREWING_STAND)
                    .putInt("x", (int) this.x)
                    .putInt("y", (int) this.y)
                    .putInt("z", (int) this.z);
                brewing = new BlockEntityBrewingStand(this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
            }

            if (brewing.namedTag.contains("Lock") && brewing.namedTag.get("Lock") instanceof StringTag) {
                if (!brewing.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return false;
                }
            }

            player.addWindow(brewing.getInventory());
        }

        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBrewingStand();
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.IRON_BLOCK_COLOR;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityBrewingStand) {
            return ContainerInventory.calculateRedstone(((BlockEntityBrewingStand) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
