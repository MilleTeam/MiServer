package cn.nukkit.network.protocol;

import cn.nukkit.raknet.protocol.EncapsulatedPacket;
import cn.nukkit.utils.BinaryStream;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class DataPacket extends BinaryStream implements Cloneable
{

	public boolean isEncoded = false;

	public EncapsulatedPacket encapsulatedPacket;

	public byte reliability;

	public Integer orderIndex = null;

	public Integer orderChannel = null;

	private int channel = 0;

	public abstract byte pid();

	public abstract void decode();

	public abstract void encode();

	@Override
	public void reset()
	{
		super.reset();
		this.putByte(this.pid());
	}

	public int getChannel()
	{
		return channel;
	}

	public void setChannel(int channel)
	{
		this.channel = channel;
	}

	public DataPacket clean()
	{
		this.setBuffer(null);
		this.setOffset(0);
		this.isEncoded = false;
		return this;
	}

	@Override
	public DataPacket clone()
	{
		try
		{
			return (DataPacket) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}

}
