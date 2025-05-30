package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

/**
 * Created by CreeperFace on 18.3.2017.
 */
public class PlayerMapInfoRequestEvent extends PlayerEvent implements Cancellable
{

	private static final HandlerList handlers = new HandlerList();

	private Item item;

	public PlayerMapInfoRequestEvent(
		Player p,
		Item item
	)
	{
		this.player = p;
		this.item = item;
	}

	public static HandlerList getHandlers()
	{
		return handlers;
	}

	public Item getMap()
	{
		return item;
	}

}
