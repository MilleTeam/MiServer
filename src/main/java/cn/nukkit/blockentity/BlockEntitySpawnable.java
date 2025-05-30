package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;

import java.io.IOException;
import java.nio.ByteOrder;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class BlockEntitySpawnable extends BlockEntity
{

	public BlockEntitySpawnable(
		FullChunk chunk,
		CompoundTag nbt
	)
	{
		super(chunk, nbt);
		this.spawnToAll();
	}

	public abstract CompoundTag getSpawnCompound();

	public void spawnTo(Player player)
	{
		if (this.closed)
		{
			return;
		}

		CompoundTag tag = this.getSpawnCompound();
		BlockEntityDataPacket pk = new BlockEntityDataPacket();
		pk.x = (int) this.x;
		pk.y = (int) this.y;
		pk.z = (int) this.z;
		try
		{
			pk.namedTag = NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN, true);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		player.dataPacket(pk);
	}

	public void spawnToAll()
	{
		if (this.closed)
		{
			return;
		}

		for (Player player : this.getLevel().getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values())
		{
			if (player.spawned)
			{
				this.spawnTo(player);
			}
		}
	}

}
