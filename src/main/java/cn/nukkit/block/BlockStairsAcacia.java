package cn.nukkit.block;

/**
 * author: MagicDroidX Nukkit Project
 */
public class BlockStairsAcacia extends BlockStairsWood
{

	public BlockStairsAcacia()
	{
		this(0);
	}

	public BlockStairsAcacia(int meta)
	{
		super(meta);
	}

	@Override
	public int getId()
	{
		return ACACIA_WOOD_STAIRS;
	}

	@Override
	public String getName()
	{
		return "Acacia Wood Stairs";
	}

}
