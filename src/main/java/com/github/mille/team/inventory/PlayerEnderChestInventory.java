package com.github.mille.team.inventory;

import com.github.mille.team.Player;
import com.github.mille.team.block.BlockEnderChest;
import com.github.mille.team.entity.EntityHuman;
import com.github.mille.team.entity.EntityHumanType;
import com.github.mille.team.level.Level;
import com.github.mille.team.network.protocol.BlockEventPacket;
import com.github.mille.team.network.protocol.ContainerClosePacket;
import com.github.mille.team.network.protocol.ContainerOpenPacket;

public class PlayerEnderChestInventory extends BaseInventory {

    public PlayerEnderChestInventory(EntityHumanType player) {
        super(player, InventoryType.ENDER_CHEST);
    }

    @Override
    public EntityHuman getHolder() {
        return (EntityHuman) this.holder;
    }

    @Override
    public void onOpen(Player who) {
        if (who != this.getHolder()) {
            return;
        }
        super.onOpen(who);
        ContainerOpenPacket containerOpenPacket = new ContainerOpenPacket();
        containerOpenPacket.windowid = (byte) who.getWindowId(this);
        containerOpenPacket.type = (byte) this.getType().getNetworkType();
        BlockEnderChest chest = who.getViewingEnderChest();
        if (chest != null) {
            containerOpenPacket.x = (int) chest.getX();
            containerOpenPacket.y = (int) chest.getY();
            containerOpenPacket.z = (int) chest.getZ();
        } else {
            containerOpenPacket.x = containerOpenPacket.y = containerOpenPacket.z = 0;
        }

        who.dataPacket(containerOpenPacket);

        this.sendContents(who);

        if (chest != null && chest.getViewers().size() == 1) {
            BlockEventPacket blockEventPacket = new BlockEventPacket();
            blockEventPacket.x = (int) chest.getX();
            blockEventPacket.y = (int) chest.getY();
            blockEventPacket.z = (int) chest.getZ();
            blockEventPacket.case1 = 1;
            blockEventPacket.case2 = 2;

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, blockEventPacket);
            }
        }
    }

    @Override
    public void onClose(Player who) {
        ContainerClosePacket containerClosePacket = new ContainerClosePacket();
        containerClosePacket.windowid = (byte) who.getWindowId(this);
        who.dataPacket(containerClosePacket);
        super.onClose(who);

        BlockEnderChest chest = who.getViewingEnderChest();
        if (chest != null && chest.getViewers().size() == 1) {
            BlockEventPacket blockEventPacket = new BlockEventPacket();
            blockEventPacket.x = (int) chest.getX();
            blockEventPacket.y = (int) chest.getY();
            blockEventPacket.z = (int) chest.getZ();
            blockEventPacket.case1 = 1;
            blockEventPacket.case2 = 0;

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, blockEventPacket);
            }

            who.setViewingEnderChest(null);
        }

        super.onClose(who);
    }

}
