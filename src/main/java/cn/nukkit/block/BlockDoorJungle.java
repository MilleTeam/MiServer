package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorJungle;

public class BlockDoorJungle extends BlockDoorWood
{

	public BlockDoorJungle()
	{
		this(0);
	}

	public BlockDoorJungle(int meta)
	{
		super(meta);
	}

	@Override
	public String getName()
	{
		return "Jungle Door Block";
	}

	@Override
	public int getId()
	{
		return JUNGLE_DOOR_BLOCK;
	}

	@Override
	public Item toItem()
	{
		return new ItemDoorJungle();
	}

}
