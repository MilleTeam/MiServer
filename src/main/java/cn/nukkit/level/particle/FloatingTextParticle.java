package cn.nukkit.level.particle;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/11/21 by xtypr. Package cn.nukkit.level.particle in project Nukkit .
 */
public class FloatingTextParticle extends Particle
{

	protected String text;

	protected String title;

	protected long entityId = -1;

	protected boolean invisible = false;

	protected EntityMetadata metadata = new EntityMetadata();

	public FloatingTextParticle(
		Vector3 pos,
		String text
	)
	{
		this(pos, text, "");
	}

	public FloatingTextParticle(
		Vector3 pos,
		String text,
		String title
	)
	{
		super(pos.x, pos.y, pos.z);
		this.text = text;
		this.title = title;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public boolean isInvisible()
	{
		return invisible;
	}

	public void setInvisible(boolean invisible)
	{
		this.invisible = invisible;
	}

	public void setInvisible()
	{
		this.setInvisible(true);
	}

	@Override
	public DataPacket[] encode()
	{
		ArrayList<DataPacket> packets = new ArrayList<>();

		if (this.entityId == -1)
		{
			this.entityId = 1095216660480L + ThreadLocalRandom.current().nextLong(0, 0x7fffffffL);
		} else
		{
			RemoveEntityPacket pk = new RemoveEntityPacket();
			pk.eid = this.entityId;

			packets.add(pk);
		}

		if (!this.invisible)
		{
			AddEntityPacket pk = new AddEntityPacket();
			pk.entityUniqueId = this.entityId;
			pk.entityRuntimeId = this.entityId;
			pk.type = EntityItem.NETWORK_ID;
			pk.x = (float) this.x;
			pk.y = (float) (this.y - 0.75);
			pk.z = (float) this.z;
			pk.speedX = 0;
			pk.speedY = 0;
			pk.speedZ = 0;
			pk.yaw = 0;
			pk.pitch = 0;
			long flags = (
				(1L << Entity.DATA_FLAG_CAN_SHOW_NAMETAG) |
				(1L << Entity.DATA_FLAG_ALWAYS_SHOW_NAMETAG) |
				(1L << Entity.DATA_FLAG_IMMOBILE)
			);
			pk.metadata = new EntityMetadata()
				.putLong(Entity.DATA_FLAGS, flags)
				.putString(Entity.DATA_NAMETAG, this.title + (!this.text.isEmpty() ? "\n" + this.text : ""));
			packets.add(pk);
		}

		return packets.stream().toArray(DataPacket[]::new);
	}

}
