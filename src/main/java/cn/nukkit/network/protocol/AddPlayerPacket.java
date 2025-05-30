package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Binary;

import java.util.UUID;

/**
 * author: MagicDroidX Nukkit Project
 */
public class AddPlayerPacket extends DataPacket
{

	public static final byte NETWORK_ID = ProtocolInfo.ADD_PLAYER_PACKET;

	public UUID uuid;

	public String username;

	public long entityUniqueId;

	public long entityRuntimeId;

	public float x;

	public float y;

	public float z;

	public float speedX;

	public float speedY;

	public float speedZ;

	public float pitch;

	public float yaw;

	public Item item;

	public EntityMetadata metadata = new EntityMetadata();

	@Override
	public byte pid()
	{
		return NETWORK_ID;
	}

	@Override
	public void decode()
	{

	}

	@Override
	public void encode()
	{
		this.reset();
		this.putUUID(this.uuid);
		this.putString(this.username);
		this.putVarLong(this.entityUniqueId);
		this.putVarLong(this.entityRuntimeId);
		this.putVector3f(this.x, this.y, this.z);
		this.putVector3f(this.speedX, this.speedY, this.speedZ);
		this.putLFloat(this.pitch);
		this.putLFloat(this.yaw); //TODO headrot
		this.putLFloat(this.yaw);
		this.putSlot(this.item);

		this.put(Binary.writeMetadata(this.metadata));
	}

}
