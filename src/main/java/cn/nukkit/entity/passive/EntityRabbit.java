package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * Author: BeYkeRYkt Nukkit Project
 */
public class EntityRabbit extends EntityAnimal
{

	public static final int NETWORK_ID = 18;

	public EntityRabbit(
		FullChunk chunk,
		CompoundTag nbt
	)
	{
		super(chunk, nbt);
	}

	@Override
	public float getWidth()
	{
		if (this.isBaby())
		{
			return 0.2f;
		}
		return 0.4f;
	}

	@Override
	public float getHeight()
	{
		if (this.isBaby())
		{
			return 0.25f;
		}
		return 0.5f;
	}

	@Override
	public float getEyeHeight()
	{
		if (isBaby())
		{
			return 0.25f;
		}
		return 0.5f;
	}

	@Override
	public String getName()
	{
		return this.getNameTag();
	}

	@Override
	public Item[] getDrops()
	{
		return new Item[] { Item.get(Item.RAW_RABBIT), Item.get(Item.RABBIT_HIDE), Item.get(Item.RABBIT_FOOT) };
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
		setMaxHealth(10);
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
