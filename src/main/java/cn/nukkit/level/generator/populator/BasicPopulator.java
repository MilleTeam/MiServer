package cn.nukkit.level.generator.populator;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

public abstract class BasicPopulator
{

	public abstract boolean generate(
		ChunkManager level,
		NukkitRandom rand,
		Vector3 position
	);

	public void setDecorationDefaults()
	{
	}

	protected void setBlockAndNotifyAdequately(
		ChunkManager level,
		BlockVector3 pos,
		Block state
	)
	{
		setBlock(level, new Vector3(pos.x, pos.y, pos.z), state);
	}

	protected void setBlockAndNotifyAdequately(
		ChunkManager level,
		Vector3 pos,
		Block state
	)
	{
		setBlock(level, pos, state);
	}

	protected void setBlock(
		ChunkManager level,
		Vector3 v,
		Block b
	)
	{
		level.setBlockIdAt((int) v.x, (int) v.y, (int) v.z, b.getId());
		level.setBlockDataAt((int) v.x, (int) v.y, (int) v.z, b.getDamage());
	}

}
