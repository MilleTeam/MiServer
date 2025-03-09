package com.github.mille.team.entity.item;

import com.github.mille.team.Player;
import com.github.mille.team.entity.Entity;
import com.github.mille.team.entity.EntityExplosive;
import com.github.mille.team.entity.data.IntEntityData;
import com.github.mille.team.event.entity.EntityDamageEvent;
import com.github.mille.team.event.entity.EntityDamageEvent.DamageCause;
import com.github.mille.team.event.entity.EntityExplosionPrimeEvent;
import com.github.mille.team.level.Explosion;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.level.sound.TNTPrimeSound;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.network.protocol.AddEntityPacket;

/**
 * @author MagicDroidX
 */
public class EntityPrimedTNT extends Entity implements EntityExplosive {

    public static final int NETWORK_ID = 65;

    protected int fuse;

    public EntityPrimedTNT(
        FullChunk chunk,
        CompoundTag nbt
    ) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.98f;
    }

    @Override
    public float getLength() {
        return 0.98f;
    }

    @Override
    public float getHeight() {
        return 0.98f;
    }

    @Override
    protected float getGravity() {
        return 0.04f;
    }

    @Override
    protected float getDrag() {
        return 0.02f;
    }

    @Override
    protected float getBaseOffset() {
        return 0.49f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    protected void initEntity() {
        super.initEntity();

        if (namedTag.contains("Fuse")) {
            fuse = namedTag.getByte("Fuse");
        } else {
            fuse = 80;
        }

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_IGNITED, true);
        this.setDataProperty(new IntEntityData(DATA_FUSE_LENGTH, fuse));

        this.level.addSound(new TNTPrimeSound(this));
    }


    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public void saveNBT() {
        super.saveNBT();
        namedTag.putByte("Fuse", fuse);
    }

    public boolean onUpdate(int currentTick) {

        if (closed) {
            return false;
        }

        int tickDiff = currentTick - lastUpdate;

        if (tickDiff <= 0 && !justCreated) {
            return true;
        }

        if (fuse % 5 == 0) {
            this.setDataProperty(new IntEntityData(DATA_FUSE_LENGTH, fuse));
        }

        lastUpdate = currentTick;

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (isAlive()) {

            motionY -= getGravity();

            move(motionX, motionY, motionZ);

            float friction = 1 - getDrag();

            motionX *= friction;
            motionY *= friction;
            motionZ *= friction;

            updateMovement();

            if (onGround) {
                motionY *= -0.5;
                motionX *= 0.7;
                motionZ *= 0.7;
            }

            fuse -= tickDiff;

            if (fuse <= 0) {
                explode();
                kill();
            }

        }

        return hasUpdate || fuse >= 0 || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001;
    }

    public void explode() {
        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, 4);
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Explosion explosion = new Explosion(this, event.getForce(), this);
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
    }

    public void spawnTo(Player player) {
        AddEntityPacket packet = new AddEntityPacket();
        packet.type = EntityPrimedTNT.NETWORK_ID;
        packet.entityUniqueId = this.getId();
        packet.entityRuntimeId = getId();
        packet.x = (float) x;
        packet.y = (float) y;
        packet.z = (float) z;
        packet.speedX = (float) motionX;
        packet.speedY = (float) motionY;
        packet.speedZ = (float) motionZ;
        packet.metadata = dataProperties;
        player.dataPacket(packet);
        super.spawnTo(player);
    }

}