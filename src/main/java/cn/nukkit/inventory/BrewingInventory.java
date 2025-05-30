package cn.nukkit.inventory;


import cn.nukkit.blockentity.BlockEntityBrewingStand;
import cn.nukkit.item.Item;

public class BrewingInventory extends ContainerInventory
{

	public BrewingInventory(BlockEntityBrewingStand brewingStand)
	{
		super(brewingStand, InventoryType.BREWING_STAND);
	}

	@Override
	public BlockEntityBrewingStand getHolder()
	{
		return (BlockEntityBrewingStand) this.holder;
	}

	public Item getIngredient()
	{
		return getItem(0);
	}

	public void setIngredient(Item item)
	{
		setItem(0, item);
	}

	@Override
	public void onSlotChange(
		int index,
		Item before,
		boolean send
	)
	{
		super.onSlotChange(index, before, send);

		this.getHolder().scheduleUpdate();
	}

}