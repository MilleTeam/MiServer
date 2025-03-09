package com.github.mille.team.entity.item;

import com.github.mille.team.block.BlockTNT;
import com.github.mille.team.entity.EntityExplosive;
import com.github.mille.team.event.entity.EntityExplosionPrimeEvent;
import com.github.mille.team.item.ItemMinecartTNT;
import com.github.mille.team.level.Explosion;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.utils.MinecartType;

import java.util.Random;

/**
 * Author: Adam Matthew [larryTheCoder]
 * <p>
 * Nukkit Project.
 */
public class EntityMinecartTNT extends EntityMinecartAbstract implements EntityExplosive {

    public static final int NETWORK_ID = 97; //wtf?

    private int fuse;

    private boolean activated;

    public EntityMinecartTNT(
        FullChunk chunk,
        CompoundTag nbt
    ) {
        super(chunk, nbt);
        super.setDisplayBlock(new BlockTNT());
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.fuse = namedTag.getInt("TNTFuse");
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_CHARGED, false);
    }

    @Override
    public void activate(
        int i,
        int j,
        int k,
        boolean flag
    ) {
        // TODO: Find out why minecart doesnt have a right TNT fuse length
        // Could be implemented in the future!
    }

    @Override
    public void explode() {
        explode(0);
    }

    public void explode(double square) {
        double root = Math.sqrt(square);

        if (root > 5.0D) {
            root = 5.0D;
        }

        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, (4.0D + new Random().nextDouble() * 1.5D * root));
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Explosion explosion = new Explosion(this, event.getForce(), this);
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
        kill();
    }

    @Override
    public void dropItem() {
        level.dropItem(this, new ItemMinecartTNT());
    }

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(3);
    }

    @Override
    public int getNetworkId() {
        return EntityMinecartTNT.NETWORK_ID;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        super.namedTag.putInt("TNTFuse", this.fuse);
    }

}
