package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * @author PikyCZ
 */
public class EntityWitherSkeleton extends EntityMob
{

	public static final int NETWORK_ID = 48;

	public EntityWitherSkeleton(
		FullChunk chunk,
		CompoundTag nbt
	)
	{
		super(chunk, nbt);
	}

	@Override
	public int getNetworkId()
	{
		return NETWORK_ID;
	}

	@Override
	protected void initEntity()
	{
		super.initEntity();
	}

	@Override
	public float getWidth()
	{
		return 0.65f;
	}

	@Override
	public float getHeight()
	{
		return 1.8f;
	}

	@Override
	public String getName()
	{
		return "WitherSkeleton";
	}

	@Override
	public void spawnTo(Player player)
	{
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
