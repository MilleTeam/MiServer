package com.github.mille.team.block;

import com.github.mille.team.Player;
import com.github.mille.team.block.entity.BlockEntity;
import com.github.mille.team.block.entity.BlockEntitySign;
import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemSign;
import com.github.mille.team.item.ItemTool;
import com.github.mille.team.level.Level;
import com.github.mille.team.math.AxisAlignedBB;
import com.github.mille.team.math.BlockFace;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.nbt.tag.Tag;
import com.github.mille.team.utils.BlockColor;

/**
 * @author Nukkit Project Team
 */
public class BlockSignPost extends BlockTransparent {

    public BlockSignPost() {
        this(0);
    }

    public BlockSignPost(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SIGN_POST;
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
        return "Sign Post";
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
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
        if (face != BlockFace.DOWN) {
            CompoundTag nbt = new CompoundTag()
                .putString("id", BlockEntity.SIGN)
                .putInt("x", (int) block.x)
                .putInt("y", (int) block.y)
                .putInt("z", (int) block.z)
                .putString("Text1", "")
                .putString("Text2", "")
                .putString("Text3", "")
                .putString("Text4", "");

            if (face == BlockFace.UP) {
                meta = (int) Math.floor(((player.yaw + 180) * 16 / 360) + 0.5) & 0x0f;
                getLevel().setBlock(block, new BlockSignPost(meta), true);
            } else {
                meta = face.getIndex();
                getLevel().setBlock(block, new BlockWallSign(meta), true);
            }

            if (player != null) {
                nbt.putString("Creator", player.getUniqueId().toString());
            }

            if (item.hasCustomBlockData()) {
                for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                    nbt.put(aTag.getName(), aTag);
                }
            }

            new BlockEntitySign(getLevel().getChunk((int) block.x >> 4, (int) block.z >> 4), nbt);

            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (down().getId() == Block.AIR) {
                getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemSign();
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

}
