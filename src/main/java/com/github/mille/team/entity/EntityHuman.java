package com.github.mille.team.entity;

import com.github.mille.team.Player;
import com.github.mille.team.entity.data.IntPositionEntityData;
import com.github.mille.team.entity.data.Skin;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.network.protocol.AddPlayerPacket;
import com.github.mille.team.network.protocol.RemoveEntityPacket;
import com.github.mille.team.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * author: MagicDroidX Nukkit Project
 */
public class EntityHuman extends EntityHumanType {

    public static final int DATA_PLAYER_FLAG_SLEEP = 1; //TODO: CHECK

    public static final int DATA_PLAYER_FLAG_DEAD = 2; //TODO: CHECK

    public static final int DATA_PLAYER_FLAGS = 27;

    public static final int DATA_PLAYER_BED_POSITION = 17;

    public static final int DATA_PLAYER_BUTTON_TEXT = 40;

    protected UUID uuid;

    protected byte[] rawUUID;

    protected Skin skin;

    public EntityHuman(
        FullChunk chunk,
        CompoundTag nbt
    ) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getLength() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    @Override
    public float getEyeHeight() {
        return 1.62f;
    }

    @Override
    protected float getBaseOffset() {
        return this.getEyeHeight();
    }

    @Override
    public int getNetworkId() {
        return -1;
    }

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public byte[] getRawUniqueId() {
        return rawUUID;
    }

    @Override
    protected void initEntity() {
        this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, false);

        this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, 0, 0, 0), false);

        if (!(this instanceof Player)) {
            if (this.namedTag.contains("NameTag")) {
                this.setNameTag(this.namedTag.getString("NameTag"));
            }

            if (this.namedTag.contains("Skin") && this.namedTag.get("Skin") instanceof CompoundTag) {
                if (!this.namedTag.getCompound("Skin").contains("Transparent")) {
                    this.namedTag.getCompound("Skin").putBoolean("Transparent", false);
                }
                this.setSkin(new Skin(this.namedTag.getCompound("Skin").getByteArray("Data"), this.namedTag.getCompound("Skin").getString("ModelId")));
            }

            this.uuid = Utils.dataToUUID(String.valueOf(this.getId()).getBytes(StandardCharsets.UTF_8), this.getSkin()
                .getData(), this.getNameTag().getBytes(StandardCharsets.UTF_8));
        }

        super.initEntity();

        if (this instanceof Player) {
            ((Player) this).addWindow(this.inventory, 0);
        }
    }

    @Override
    public String getName() {
        return this.getNameTag();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (this.getSkin().getData().length > 0) {
            this.namedTag.putCompound("Skin", new CompoundTag()
                .putByteArray("Data", this.getSkin().getData())
                .putString("ModelId", this.getSkin().getModel())
            );
        }
    }

    @Override
    public void spawnTo(Player player) {
        if (this != player && !this.hasSpawned.containsKey(player.getLoaderId())) {
            this.hasSpawned.put(player.getLoaderId(), player);

            if (this.skin.getData().length < 64 * 32 * 4) {
                throw new IllegalStateException(this.getClass().getSimpleName() + " must have a valid skin set");
            }

            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getName(), this.skin, new Player[]{player});

            AddPlayerPacket pk = new AddPlayerPacket();
            pk.uuid = this.getUniqueId();
            pk.username = this.getName();
            pk.entityUniqueId = this.getId();
            pk.entityRuntimeId = this.getId();
            pk.x = (float) this.x;
            pk.y = (float) this.y;
            pk.z = (float) this.z;
            pk.speedX = (float) this.motionX;
            pk.speedY = (float) this.motionY;
            pk.speedZ = (float) this.motionZ;
            pk.yaw = (float) this.yaw;
            pk.pitch = (float) this.pitch;
            pk.item = this.getInventory().getItemInHand();
            pk.metadata = this.dataProperties;
            player.dataPacket(pk);

            this.inventory.sendArmorContents(player);
            this.offhandInventory.sendContents(player);

            if (!(this instanceof Player)) {
                this.server.removePlayerListData(this.getUniqueId(), new Player[]{player});
            }
        }
    }

    @Override
    public void despawnFrom(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {

            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.getId();
            player.dataPacket(pk);
            this.hasSpawned.remove(player.getLoaderId());
        }
    }

    @Override
    public void close() {
        if (!this.closed) {
            if (this.getFloatingInventory() != null) {
                this.getFloatingInventory().getContents().forEach((id, item) -> {
                    this.level.dropItem(this, item);
                });
            } else {
                this.getServer().getLogger().debug("Attempted to drop a null crafting inventory");
            }
            if (this.inventory != null) {
                this.inventory.removeAllViewers();
                this.inventory = null;
            }
            if (this.enderChestInventory != null) {
                this.enderChestInventory.removeAllViewers();
                this.enderChestInventory = null;
            }
            super.close();
        }
    }

}
