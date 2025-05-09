package cn.nukkit.block;

/**
 * Created on 2015/11/25 by xtypr. Package cn.nukkit.block in project Nukkit .
 */
public class BlockStairsSpruce extends BlockStairsWood
{

	public BlockStairsSpruce()
	{
		this(0);
	}

	public BlockStairsSpruce(int meta)
	{
		super(meta);
	}

	@Override
	public int getId()
	{
		return SPRUCE_WOOD_STAIRS;
	}

	@Override
	public String getName()
	{
		return "Spruce Wood Stairs";
	}

}
