package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX Nukkit Project
 */
public class BlockEventPacket extends DataPacket
{

	public static final byte NETWORK_ID = ProtocolInfo.BLOCK_EVENT_PACKET;

	public int x;

	public int y;

	public int z;

	public int case1;

	public int case2;

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
		this.putBlockCoords(this.x, this.y, this.z);
		this.putVarInt(this.case1);
		this.putVarInt(this.case2);
	}

}
