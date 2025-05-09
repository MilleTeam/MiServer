package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX Nukkit Project
 */
public class SetPlayerGameTypePacket extends DataPacket
{

	public final static byte NETWORK_ID = ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET;

	public int gamemode;

	@Override
	public byte pid()
	{
		return NETWORK_ID;
	}

	@Override
	public void decode()
	{
		this.gamemode = this.getVarInt();
	}

	@Override
	public void encode()
	{
		this.reset();
		this.putVarInt(this.gamemode);
	}

}
