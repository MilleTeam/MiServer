package cn.nukkit.event.inventory;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.TransactionGroup;

/**
 * author: MagicDroidX Nukkit Project
 */
public class InventoryTransactionEvent extends Event implements Cancellable
{

	private static final HandlerList handlers = new HandlerList();

	private final TransactionGroup transaction;

	public InventoryTransactionEvent(TransactionGroup transaction)
	{
		this.transaction = transaction;
	}

	public static HandlerList getHandlers()
	{
		return handlers;
	}

	public TransactionGroup getTransaction()
	{
		return transaction;
	}

}