package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBed;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBed;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;

/**
 * author: MagicDroidX Nukkit Project
 */
public class BlockBed extends BlockTransparent
{

	public BlockBed()
	{
		this(0);
	}

	public BlockBed(int meta)
	{
		super(meta);
	}

	@Override
	public int getId()
	{
		return BED_BLOCK;
	}

	@Override
	public boolean canBeActivated()
	{
		return true;
	}

	@Override
	public double getResistance()
	{
		return 1;
	}

	@Override
	public double getHardness()
	{
		return 0.2;
	}

	@Override
	public String getName()
	{
		return this.getDyeColor().getName() + " Bed Block";
	}

	@Override
	protected AxisAlignedBB recalculateBoundingBox()
	{
		return new AxisAlignedBB(
			this.x,
			this.y,
			this.z,
			this.x + 1,
			this.y + 0.5625,
			this.z + 1
		);
	}

	@Override
	public boolean onActivate(Item item)
	{
		return this.onActivate(item, null);
	}

	@Override
	public boolean onActivate(
		Item item,
		Player player
	)
	{
		int time = this.getLevel().getTime() % Level.TIME_FULL;

		boolean isNight = (time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE);

		if (player != null && !isNight)
		{
			player.sendMessage(TextFormat.GRAY + "You can only sleep at night");
			return true;
		}

		Block blockNorth = this.north();
		Block blockSouth = this.south();
		Block blockEast = this.east();
		Block blockWest = this.west();

		Block b;
		if ((this.meta & 0x08) == 0x08)
		{
			b = this;
		} else
		{
			if (blockNorth.getId() == this.getId() && (blockNorth.meta & 0x08) == 0x08)
			{
				b = blockNorth;
			} else if (blockSouth.getId() == this.getId() && (blockSouth.meta & 0x08) == 0x08)
			{
				b = blockSouth;
			} else if (blockEast.getId() == this.getId() && (blockEast.meta & 0x08) == 0x08)
			{
				b = blockEast;
			} else if (blockWest.getId() == this.getId() && (blockWest.meta & 0x08) == 0x08)
			{
				b = blockWest;
			} else
			{
				if (player != null)
				{
					player.sendMessage(TextFormat.GRAY + "This bed is incomplete");
				}

				return true;
			}
		}

		if (player != null && !player.sleepOn(b))
		{
			player.sendMessage(TextFormat.GRAY + "This bed is occupied");
		}


		return true;
	}

	@Override
	public boolean place(
		Item item,
		Block block,
		Block target,
		BlockFace face,
		double fx,
		double fy,
		double fz
	)
	{
		return this.place(item, block, target, face, fx, fy, fz, null);
	}

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
		if (!down.isTransparent())
		{
			Block next = this.getSide(player.getDirection());
			Block downNext = next.down();

			if (next.canBeReplaced() && !downNext.isTransparent())
			{
				int meta = player.getDirection().getHorizontalIndex();

				this.getLevel().setBlock(block, Block.get(this.getId(), meta), false, true);
				this.getLevel().setBlock(next, Block.get(this.getId(), meta | 0x08), false, true);

				createBlockEntity(this, item.getDamage());
				createBlockEntity(next, item.getDamage());
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean onBreak(Item item)
	{
		Block blockNorth = this.north(); //Gets the blocks around them
		Block blockSouth = this.south();
		Block blockEast = this.east();
		Block blockWest = this.west();

		if ((this.meta & 0x08) == 0x08)
		{ //This is the Top part of bed
			if (blockNorth.getId() == this.getId() && blockNorth.meta != 0x08)
			{ //Checks if the block ID&&meta are right
				this.getLevel().setBlock(blockNorth, new BlockAir(), true, true);
			} else if (blockSouth.getId() == this.getId() && blockSouth.meta != 0x08)
			{
				this.getLevel().setBlock(blockSouth, new BlockAir(), true, true);
			} else if (blockEast.getId() == this.getId() && blockEast.meta != 0x08)
			{
				this.getLevel().setBlock(blockEast, new BlockAir(), true, true);
			} else if (blockWest.getId() == this.getId() && blockWest.meta != 0x08)
			{
				this.getLevel().setBlock(blockWest, new BlockAir(), true, true);
			}
		} else
		{ //Bottom Part of Bed
			if (blockNorth.getId() == this.getId() && (blockNorth.meta & 0x08) == 0x08)
			{
				this.getLevel().setBlock(blockNorth, new BlockAir(), true, true);
			} else if (blockSouth.getId() == this.getId() && (blockSouth.meta & 0x08) == 0x08)
			{
				this.getLevel().setBlock(blockSouth, new BlockAir(), true, true);
			} else if (blockEast.getId() == this.getId() && (blockEast.meta & 0x08) == 0x08)
			{
				this.getLevel().setBlock(blockEast, new BlockAir(), true, true);
			} else if (blockWest.getId() == this.getId() && (blockWest.meta & 0x08) == 0x08)
			{
				this.getLevel().setBlock(blockWest, new BlockAir(), true, true);
			}
		}
		this.getLevel().setBlock(this, new BlockAir(), true, true);

		return true;
	}

	private void createBlockEntity(
		Vector3 pos,
		int color
	)
	{
		CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntity.BED);
		nbt.putByte("color", color);

		new BlockEntityBed(this.level.getChunk(pos.getFloorX() >> 4, pos.getFloorZ() >> 4), nbt);
	}

	@Override
	public Item toItem()
	{
		return new ItemBed(this.getDyeColor().getWoolData());
	}

	@Override
	public BlockColor getColor()
	{
		return this.getDyeColor().getColor();
	}

	public DyeColor getDyeColor()
	{
		BlockEntity blockEntity = this.level.getBlockEntity(this);

		if (blockEntity instanceof BlockEntityBed)
		{
			return ((BlockEntityBed) blockEntity).getDyeColor();
		}
		return DyeColor.WHITE;
	}

}
