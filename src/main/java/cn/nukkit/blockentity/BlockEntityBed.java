package cn.nukkit.blockentity;

import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockEntityBed extends BlockEntitySpawnable
{

	public int color;

	public BlockEntityBed(
		FullChunk chunk,
		CompoundTag nbt
	)
	{
		super(chunk, nbt);

		if (!this.namedTag.contains("color"))
		{
			this.namedTag.putByte("color", 0);
		}

		this.color = this.namedTag.getByte("color");
	}

	@Override
	public boolean isBlockEntityValid()
	{
		return this.level.getBlockIdAt(this.getFloorX(), this.getFloorY(), this.getFloorZ()) == Item.BED_BLOCK;
	}

	@Override
	public void saveNBT()
	{
		super.saveNBT();
		this.namedTag.putByte("color", this.color);
	}

	@Override
	public CompoundTag getSpawnCompound()
	{
		return new CompoundTag()
			.putString("id", BlockEntity.BED)
			.putInt("x", (int) this.x)
			.putInt("y", (int) this.y)
			.putInt("z", (int) this.z)
			.putByte("color", this.color);
	}

	public DyeColor getDyeColor()
	{
		return DyeColor.getByDyeData(color);
	}

}
