package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class AddPaintingPacket extends DataPacket
{

	public static final byte NETWORK_ID = ProtocolInfo.ADD_PAINTING_PACKET;

	public long entityUniqueId;

	public long entityRuntimeId;

	public int x;

	public int y;

	public int z;

	public int direction;

	public String title;

	@Override
	public void decode()
	{

	}

	@Override
	public void encode()
	{
		this.reset();
		this.putVarLong(this.entityUniqueId);
		this.putVarLong(this.entityRuntimeId);
		this.putBlockCoords(this.x, this.y, this.z);
		this.putVarInt(this.direction);
		this.putString(this.title);
	}

	@Override
	public byte pid()
	{
		return NETWORK_ID;
	}

}
