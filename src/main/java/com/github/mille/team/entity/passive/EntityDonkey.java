package com.github.mille.team.entity.passive;

import com.github.mille.team.Player;
import com.github.mille.team.item.Item;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.network.protocol.AddEntityPacket;

/**
 * @author PikyCZ
 */
public class EntityDonkey extends EntityAnimal {

    public static final int NETWORK_ID = 24;

    public EntityDonkey(
        FullChunk chunk,
        CompoundTag nbt
    ) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.6982f;
        }
        return 1.3965f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.8f;
        }
        return 1.6f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(15);
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.LEATHER)};
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = this.getNetworkId();
        pk.entityUniqueId = this.getId();
        pk.entityRuntimeId = this.getId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);

        super.spawnTo(player);
    }

}
