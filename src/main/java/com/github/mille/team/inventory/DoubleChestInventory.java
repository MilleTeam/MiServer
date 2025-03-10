package com.github.mille.team.inventory;

import com.github.mille.team.Player;
import com.github.mille.team.block.entity.BlockEntityChest;
import com.github.mille.team.item.Item;
import com.github.mille.team.level.Level;
import com.github.mille.team.network.protocol.BlockEventPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX Nukkit Project
 */
public class DoubleChestInventory extends ContainerInventory implements InventoryHolder {

    private final ChestInventory left;

    private final ChestInventory right;

    public DoubleChestInventory(
        BlockEntityChest left,
        BlockEntityChest right
    ) {
        super(null, InventoryType.DOUBLE_CHEST);
        this.holder = this;

        this.left = left.getRealInventory();
        this.right = right.getRealInventory();

        Map<Integer, Item> items = new HashMap<>();
        // First we add the items from the left chest
        for (int idx = 0; idx < this.left.getSize(); idx++) {
            if (this.left.getContents().containsKey(idx)) { // Don't forget to skip empty slots!
                items.put(idx, this.left.getContents().get(idx));
            }
        }
        // And them the items from the right chest
        for (int idx = 0; idx < this.right.getSize(); idx++) {
            if (this.right.getContents().containsKey(idx)) { // Don't forget to skip empty slots!
                items.put(idx + this.left.getSize(), this.right.getContents().get(idx)); // idx + this.left.getSize() so we don't overlap left chest items
            }
        }

        this.setContents(items);
    }

    @Override
    public Inventory getInventory() {
        return this;
    }

    @Override
    public BlockEntityChest getHolder() {
        return this.left.getHolder();
    }

    @Override
    public Item getItem(int index) {
        return index < this.left.getSize() ? this.left.getItem(index) : this.right.getItem(index - this.right.getSize());
    }

    @Override
    public boolean setItem(
        int index,
        Item item
    ) {
        return index < this.left.getSize() ? this.left.setItem(index, item) : this.right.setItem(index - this.right.getSize(), item);
    }

    @Override
    public boolean clear(int index) {
        return index < this.left.getSize() ? this.left.clear(index) : this.right.clear(index - this.right.getSize());
    }

    @Override
    public Map<Integer, Item> getContents() {
        Map<Integer, Item> contents = new HashMap<>();

        for (int i = 0; i < this.getSize(); ++i) {
            contents.put(i, this.getItem(i));
        }

        return contents;
    }

    @Override
    public void setContents(Map<Integer, Item> items) {
        if (items.size() > this.size) {
            Map<Integer, Item> newItems = new HashMap<>();
            for (int i = 0; i < this.size; i++) {
                newItems.put(i, items.get(i));
            }
            items = newItems;
        }

        for (int i = 0; i < this.size; i++) {
            if (!items.containsKey(i)) {
                if (i < this.left.size) {
                    if (this.left.slots.containsKey(i)) {
                        this.clear(i);
                    }
                } else if (this.right.slots.containsKey(i - this.left.size)) {
                    this.clear(i);
                }
            } else if (!this.setItem(i, items.get(i))) {
                this.clear(i);
            }
        }
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        if (this.getViewers().size() == 1) {
            BlockEventPacket pk1 = new BlockEventPacket();
            pk1.x = (int) this.left.getHolder().getX();
            pk1.y = (int) this.left.getHolder().getY();
            pk1.z = (int) this.left.getHolder().getZ();
            pk1.case1 = 1;
            pk1.case2 = 2;
            Level level = this.left.getHolder().getLevel();
            if (level != null) {
                level.addChunkPacket((int) this.left.getHolder().getX() >> 4, (int) this.left.getHolder().getZ() >> 4, pk1);
            }

            BlockEventPacket pk2 = new BlockEventPacket();
            pk2.x = (int) this.right.getHolder().getX();
            pk2.y = (int) this.right.getHolder().getY();
            pk2.z = (int) this.right.getHolder().getZ();
            pk2.case1 = 1;
            pk2.case2 = 2;

            level = this.right.getHolder().getLevel();
            if (level != null) {
                level.addChunkPacket((int) this.right.getHolder().getX() >> 4, (int) this.right.getHolder().getZ() >> 4, pk2);
            }
        }
    }

    @Override
    public void onClose(Player who) {
        if (this.getViewers().size() == 1) {
            BlockEventPacket pk1 = new BlockEventPacket();
            pk1.x = (int) this.right.getHolder().getX();
            pk1.y = (int) this.right.getHolder().getY();
            pk1.z = (int) this.right.getHolder().getZ();
            pk1.case1 = 1;
            pk1.case2 = 0;

            Level level = this.right.getHolder().getLevel();
            if (level != null) {
                level.addChunkPacket((int) this.right.getHolder().getX() >> 4, (int) this.right.getHolder().getZ() >> 4, pk1);
            }

            BlockEventPacket pk2 = new BlockEventPacket();
            pk2.x = (int) this.left.getHolder().getX();
            pk2.y = (int) this.left.getHolder().getY();
            pk2.z = (int) this.left.getHolder().getZ();
            pk2.case1 = 1;
            pk2.case2 = 0;

            level = this.left.getHolder().getLevel();
            if (level != null) {
                level.addChunkPacket((int) this.left.getHolder().getX() >> 4, (int) this.left.getHolder().getZ() >> 4, pk2);
            }
        }

        super.onClose(who);
    }

    public ChestInventory getLeftSide() {
        return this.left;
    }

    public ChestInventory getRightSide() {
        return this.right;
    }

}
