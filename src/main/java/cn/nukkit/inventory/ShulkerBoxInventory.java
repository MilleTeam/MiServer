package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityShulkerBox;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ShulkerBoxInventory extends ContainerInventory
{

	public ShulkerBoxInventory(BlockEntityShulkerBox shulkerBox)
	{
		super(shulkerBox, InventoryType.SHULKER_BOX);
	}

	@Override
	public BlockEntityShulkerBox getHolder()
	{
		return (BlockEntityShulkerBox) this.holder;
	}

	@Override
	public void onOpen(Player who)
	{
		super.onOpen(who);

		if (this.getViewers().size() == 1)
		{
			BlockEventPacket pk = new BlockEventPacket();
			pk.x = (int) this.getHolder().getX();
			pk.y = (int) this.getHolder().getY();
			pk.z = (int) this.getHolder().getZ();
			pk.case1 = 1;
			pk.case2 = 2;

			Level level = this.getHolder().getLevel();
			if (level != null)
			{
				level.addLevelSoundEvent(LevelSoundEventPacket.SOUND_SHULKERBOX_OPEN, 1, -1, this.getHolder().add(0.5, 0.5, 0.5), false, false);
				level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, pk);
			}
		}
	}

	@Override
	public void onClose(Player who)
	{
		if (this.getViewers().size() == 1)
		{
			BlockEventPacket pk = new BlockEventPacket();
			pk.x = (int) this.getHolder().getX();
			pk.y = (int) this.getHolder().getY();
			pk.z = (int) this.getHolder().getZ();
			pk.case1 = 1;
			pk.case2 = 0;

			Level level = this.getHolder().getLevel();
			if (level != null)
			{
				level.addLevelSoundEvent(LevelSoundEventPacket.SOUND_SHULKERBOX_CLOSED, 1, -1, this.getHolder().add(0.5, 0.5, 0.5), false, false);
				level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, pk);
			}
		}

		super.onClose(who);
	}

}
