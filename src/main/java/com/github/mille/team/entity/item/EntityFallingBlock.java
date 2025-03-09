package com.github.mille.team.entity.item;

import com.github.mille.team.Player;
import com.github.mille.team.block.Block;
import com.github.mille.team.entity.Entity;
import com.github.mille.team.entity.data.IntEntityData;
import com.github.mille.team.event.entity.EntityBlockChangeEvent;
import com.github.mille.team.event.entity.EntityDamageEvent;
import com.github.mille.team.event.entity.EntityDamageEvent.DamageCause;
import com.github.mille.team.item.Item;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.level.sound.AnvilFallSound;
import com.github.mille.team.math.Vector3;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.network.protocol.AddEntityPacket;

/**
 * @author MagicDroidX
 */
public class EntityFallingBlock extends Entity {

    public static final int NETWORK_ID = 66;

    protected int blockId;

    protected int damage;

    public EntityFallingBlock(
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
    protected void initEntity() {
        super.initEntity();

        if (namedTag != null) {
            if (namedTag.contains("TileID")) {
                blockId = namedTag.getInt("TileID");
            } else if (namedTag.contains("Tile")) {
                blockId = namedTag.getInt("Tile");
                namedTag.putInt("TileID", blockId);
            }

            if (namedTag.contains("Data")) {
                damage = namedTag.getByte("Data");
            }
        }

        if (blockId == 0) {
            close();
            return;
        }

        setDataProperty(new IntEntityData(DATA_VARIANT, this.getBlock() | this.getDamage() << 8));
    }

    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    @Override
    public boolean onUpdate(int currentTick) {

        if (closed) {
            return false;
        }

        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0 && !justCreated) {
            return true;
        }

        lastUpdate = currentTick;

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (isAlive()) {
            motionY -= getGravity();

            move(motionX, motionY, motionZ);

            float friction = 1 - getDrag();

            motionX *= friction;
            motionY *= 1 - getDrag();
            motionZ *= friction;

            Vector3 pos = (new Vector3(x - 0.5, y, z - 0.5)).round();

            if (onGround) {
                kill();
                Block block = level.getBlock(pos);
                if (block.getId() > 0 && block.isTransparent() && !block.canBeReplaced()) {
                    if (this.level.getGameRules().getBoolean("doEntityDrops")) {
                        getLevel().dropItem(this, Item.get(this.getBlock(), this.getDamage(), 1));
                    }
                } else {
                    EntityBlockChangeEvent event = new EntityBlockChangeEvent(this, block, Block.get(getBlock(), getDamage()));
                    server.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        getLevel().setBlock(pos, event.getTo(), true);

                        if (event.getTo().getId() == Item.ANVIL) {
                            getLevel().addSound(new AnvilFallSound(pos));
                        }
                    }
                }
                hasUpdate = true;
            }

            updateMovement();
        }

        return hasUpdate || !onGround || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001;
    }

    public int getBlock() {
        return blockId;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void saveNBT() {
        namedTag.putInt("TileID", blockId);
        namedTag.putByte("Data", damage);
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket packet = new AddEntityPacket();
        packet.type = EntityFallingBlock.NETWORK_ID;
        packet.entityUniqueId = this.getId();
        packet.entityRuntimeId = getId();
        packet.x = (float) x;
        packet.y = (float) y;
        packet.z = (float) z;
        packet.speedX = (float) motionX;
        packet.speedY = (float) motionY;
        packet.speedZ = (float) motionZ;
        packet.yaw = (float) yaw;
        packet.pitch = (float) pitch;
        packet.metadata = dataProperties;
        player.dataPacket(packet);
        super.spawnTo(player);
    }

}
