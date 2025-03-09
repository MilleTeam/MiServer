package com.github.mille.team.block;

import com.github.mille.team.Player;
import com.github.mille.team.Server;
import com.github.mille.team.event.block.BlockSpreadEvent;
import com.github.mille.team.item.Item;
import com.github.mille.team.level.Level;
import com.github.mille.team.level.generator.object.ObjectTallGrass;
import com.github.mille.team.math.NukkitRandom;
import com.github.mille.team.math.Vector3;
import com.github.mille.team.utils.BlockColor;

/**
 * author: Angelic47 Nukkit Project
 */
public class BlockGrass extends BlockDirt {

    public BlockGrass() {
        this(0);
    }

    public BlockGrass(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return GRASS;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public String getName() {
        return "Grass";
    }

    @Override
    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    @Override
    public boolean onActivate(
        Item item,
        Player player
    ) {
        if (item.getId() == Item.DYE && item.getDamage() == 0x0F) {
            item.count--;
            ObjectTallGrass.growGrass(this.getLevel(), this, new NukkitRandom(), 15, 10);
            return true;
        } else if (item.isHoe()) {
            item.useOn(this);
            this.getLevel().setBlock(this, new BlockFarmland());
            return true;
        } else if (item.isShovel()) {
            item.useOn(this);
            this.getLevel().setBlock(this, new BlockGrassPath());
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            Block block = this.getLevel().getBlock(new Vector3(this.x, this.y, this.z));
            if (block.up().getLightLevel() < 4) {
                BlockSpreadEvent ev = new BlockSpreadEvent(block, this, new BlockDirt());
                Server.getInstance().getPluginManager().callEvent(ev);
            } else if (block.up().getLightLevel() >= 9) {
                for (int l = 0; l < 4; ++l) {
                    NukkitRandom random = new NukkitRandom();
                    int x = random.nextRange((int) this.x - 1, (int) this.x + 1);
                    int y = random.nextRange((int) this.y - 2, (int) this.y + 2);
                    int z = random.nextRange((int) this.z - 1, (int) this.z + 1);
                    Block blocks = this.getLevel().getBlock(new Vector3(x, y, z));
                    if (blocks.getId() == Block.DIRT && blocks.getDamage() == 0x0F && blocks.up().getLightLevel() >= 4 && blocks.z <= 2) {
                        BlockSpreadEvent ev = new BlockSpreadEvent(blocks, this, new BlockGrass());
                        Server.getInstance().getPluginManager().callEvent(ev);
                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(blocks, ev.getNewState());
                        }
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRASS_BLOCK_COLOR;
    }

}
