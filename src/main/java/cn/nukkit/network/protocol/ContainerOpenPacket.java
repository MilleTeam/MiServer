package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ContainerOpenPacket extends DataPacket
{

	public static final byte NETWORK_ID = ProtocolInfo.CONTAINER_OPEN_PACKET;

	public final long entityId = -1;

	public byte windowid;

	public byte type;

	public int x;

	public int y;

	public int z;

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
		this.putByte(this.windowid);
		this.putByte(this.type);
		this.putBlockCoords(this.x, this.y, this.z);
		this.putVarLong(this.entityId);
	}

}
