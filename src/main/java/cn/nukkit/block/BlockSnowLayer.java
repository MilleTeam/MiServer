package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSnowball;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/6 by xtypr. Package cn.nukkit.block in project Nukkit .
 */
public class BlockSnowLayer extends BlockFlowable
{

	public BlockSnowLayer()
	{
		this(0);
	}

	public BlockSnowLayer(int meta)
	{
		super(meta);
	}

	@Override
	public String getName()
	{
		return "Snow Layer";
	}

	@Override
	public int getId()
	{
		return SNOW_LAYER;
	}

	@Override
	public double getHardness()
	{
		return 0.1;
	}

	@Override
	public double getResistance()
	{
		return 0.5;
	}

	@Override
	public int getToolType()
	{
		return ItemTool.TYPE_SHOVEL;
	}

	@Override
	public boolean canBeReplaced()
	{
		return true;
	}

	//TODO:雪片叠垒乐

	@Override
	public boolean place(
		Item item,
		Block block,
		Block target,
		BlockFace face,
		double fx,
		double fy,
		double fz,
		Player player
	)
	{
		Block down = this.down();
		if (down.isSolid())
		{
			this.getLevel().setBlock(block, this, true);

			return true;
		}
		return false;
	}

	@Override
	public int onUpdate(int type)
	{
		if (type == Level.BLOCK_UPDATE_NORMAL)
		{
			if (this.down().isTransparent())
			{
				this.getLevel().useBreakOn(this);

				return Level.BLOCK_UPDATE_NORMAL;
			}
		} else if (type == Level.BLOCK_UPDATE_RANDOM)
		{
			if (this.getLevel().getBlockLightAt((int) this.x, (int) this.y, (int) this.z) >= 10)
			{
				this.getLevel().setBlock(this, new BlockAir(), true);
				return Level.BLOCK_UPDATE_NORMAL;
			}
		}
		return 0;
	}

	@Override
	public Item[] getDrops(Item item)
	{
		if (item.isShovel() && item.getTier() >= ItemTool.TIER_WOODEN)
		{
			return new Item[] {
				new ItemSnowball()
			};
		} else
		{
			return new Item[0];
		}
	}

	@Override
	public BlockColor getColor()
	{
		return BlockColor.SNOW_BLOCK_COLOR;
	}

	@Override
	public boolean canHarvestWithHand()
	{
		return false;
	}

}


