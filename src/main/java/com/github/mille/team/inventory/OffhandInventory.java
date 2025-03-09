package com.github.mille.team.inventory;

import com.github.mille.team.Player;
import com.github.mille.team.entity.EntityHuman;
import com.github.mille.team.entity.EntityHumanType;
import com.github.mille.team.item.Item;
import com.github.mille.team.network.protocol.ContainerSetContentPacket;
import com.github.mille.team.network.protocol.ContainerSetSlotPacket;
import com.github.mille.team.network.protocol.MobEquipmentPacket;

public class OffhandInventory extends BaseInventory {

    public OffhandInventory(EntityHumanType holder) {
        super(holder, InventoryType.OFFHAND);
    }

    @Override
    public void setSize(int size) {
        throw new UnsupportedOperationException("Offhand can only carry one item at a time");
    }

    public void onSlotChange(
        int index,
        Item before,
        boolean send
    ) {
        EntityHuman holder = this.getHolder();
        if (holder instanceof Player && !((Player) holder).spawned) {
            return;
        }

        this.sendContents(this.getViewers());
        this.sendContents(holder.getViewers().values());
    }

    @Override
    public void sendContents(Player... players) {
        Item item = this.getItem(0);
        MobEquipmentPacket pk = this.createMobEquipmentPacket(item);

        for (Player player : players) {
            if (player == this.getHolder()) {
                ContainerSetContentPacket pk2 = new ContainerSetContentPacket();
                pk2.eid = player.getId();
                pk2.windowid = 119;
                pk2.slots = new Item[]{item};
                player.dataPacket(pk2);
            } else {
                player.dataPacket(pk);
            }
        }
    }

    @Override
    public void sendSlot(
        int index,
        Player... players
    ) {
        Item item = this.getItem(0);
        MobEquipmentPacket pk = this.createMobEquipmentPacket(item);

        for (Player player : players) {
            if (player == this.getHolder()) {
                ContainerSetSlotPacket pk2 = new ContainerSetSlotPacket();
                pk2.windowid = 119;
                pk2.hotbarSlot = 1;
                pk2.slot = 1;
                pk2.selectedSlot = 1;
                pk2.item = item;
                player.dataPacket(pk2);
            } else {
                player.dataPacket(pk);
            }
        }
    }

    private MobEquipmentPacket createMobEquipmentPacket(Item item) {
        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.eid = this.getHolder().getId();
        pk.item = item;
        pk.selectedSlot = 0;
        pk.slot = 0;
        pk.windowId = 119;
        pk.encode();
        pk.isEncoded = true;
        return pk;
    }

    @Override
    public EntityHuman getHolder() {
        return (EntityHuman) super.getHolder();
    }

}