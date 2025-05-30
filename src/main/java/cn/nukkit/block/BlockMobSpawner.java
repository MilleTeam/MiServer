package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * Created by Pub4Game on 27.12.2015.
 */
public class BlockMobSpawner extends BlockSolid
{

	public BlockMobSpawner()
	{
		this(0);
	}

	public BlockMobSpawner(int meta)
	{
		super(0);
	}

	@Override
	public String getName()
	{
		return "Monster Spawner";
	}

	@Override
	public int getId()
	{
		return MONSTER_SPAWNER;
	}

	@Override
	public int getToolType()
	{
		return ItemTool.TYPE_PICKAXE;
	}

	@Override
	public double getHardness()
	{
		return 5;
	}

	@Override
	public double getResistance()
	{
		return 25;
	}

	@Override
	public Item[] getDrops(Item item)
	{
		if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN)
		{
			return new Item[] {
				toItem()
			};
		} else
		{
			return new Item[0];
		}
	}

	@Override
	public boolean canBePushed()
	{
		return false;
	}

	@Override
	public boolean canHarvestWithHand()
	{
		return false;
	}

}
