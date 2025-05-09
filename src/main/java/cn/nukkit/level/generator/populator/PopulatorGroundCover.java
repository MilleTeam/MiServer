package cn.nukkit.level.generator.populator;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX Nukkit Project
 */
public class PopulatorGroundCover extends Populator
{

	@Override
	public void populate(
		ChunkManager level,
		int chunkX,
		int chunkZ,
		NukkitRandom random
	)
	{
		FullChunk chunk = level.getChunk(chunkX, chunkZ);
		for (int x = 0 ; x < 16 ; ++x)
		{
			for (int z = 0 ; z < 16 ; ++z)
			{
				Biome biome = Biome.getBiome(chunk.getBiomeId(x, z));
				Block[] cover = biome.getGroundCover();
				if (cover != null && cover.length > 0)
				{
					int diffY = 0;
					if (!cover[0].isSolid())
					{
						diffY = 1;
					}

					byte[] column = chunk.getBlockIdColumn(x, z);
					int y;
					for (y = 127; y > 0 ; --y)
					{
						if (column[y] != 0x00 && !Block.get(column[y] & 0xff).isTransparent())
						{
							break;
						}
					}
					int startY = Math.min(127, y + diffY);
					int endY = startY - cover.length;
					for (y = startY; y > endY && y >= 0 ; --y)
					{
						Block b = cover[startY - y];
						if (column[y] == 0x00 && b.isSolid())
						{
							break;
						}
						if (b.getDamage() == 0)
						{
							chunk.setBlockId(x, y, z, b.getId());
						} else
						{
							chunk.setBlock(x, y, z, b.getId(), b.getDamage());
						}
					}
				}
			}
		}
	}

}
