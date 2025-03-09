package com.github.mille.team.block;

import com.github.mille.team.item.Item;
import com.github.mille.team.level.Level;
import com.github.mille.team.math.BlockFace;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class BlockWallSign extends BlockSignPost {

    public BlockWallSign() {
        this(0);
    }

    public BlockWallSign(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Wall Sign";
    }

    @Override
    public int onUpdate(int type) {
        int[] faces = {
            3,
            2,
            5,
            4,
        };
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.meta >= 2 && this.meta <= 5) {
                if (this.getSide(BlockFace.fromIndex(faces[this.meta - 2])).getId() == Item.AIR) {
                    this.getLevel().useBreakOn(this);
                }
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

}
