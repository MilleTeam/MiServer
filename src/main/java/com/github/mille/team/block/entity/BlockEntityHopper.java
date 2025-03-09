package com.github.mille.team.block.entity;

import com.github.mille.team.block.Block;
import com.github.mille.team.block.BlockAir;
import com.github.mille.team.entity.Entity;
import com.github.mille.team.entity.item.EntityItem;
import com.github.mille.team.inventory.HopperInventory;
import com.github.mille.team.inventory.Inventory;
import com.github.mille.team.inventory.InventoryHolder;
import com.github.mille.team.item.Item;
import com.github.mille.team.item.ItemBlock;
import com.github.mille.team.level.format.FullChunk;
import com.github.mille.team.math.AxisAlignedBB;
import com.github.mille.team.math.BlockFace;
import com.github.mille.team.nbt.NBTIO;
import com.github.mille.team.nbt.tag.CompoundTag;
import com.github.mille.team.nbt.tag.ListTag;

/**
 * Created by CreeperFace on 8.5.2017.
 */
public class BlockEntityHopper extends BlockEntitySpawnable implements InventoryHolder, BlockEntityContainer, BlockEntityNameable {

    protected final HopperInventory inventory;
    private final AxisAlignedBB pickupArea;
    public int transferCooldown = 8;

    public BlockEntityHopper(
        FullChunk chunk,
        CompoundTag nbt
    ) {
        super(chunk, nbt);

        if (this.namedTag.contains("TransferCooldown")) {
            this.transferCooldown = this.namedTag.getInt("TransferCooldown");
        }

        this.inventory = new HopperInventory(this);

        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        }

        for (int i = 0; i < this.getSize(); i++) {
            this.inventory.setItem(i, this.getItem(i));
        }

        this.pickupArea = new AxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 2, this.z + 1);

        this.scheduleUpdate();
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.level.getBlockIdAt(this.getFloorX(), this.getFloorY(), this.getFloorZ()) == Block.HOPPER_BLOCK;
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Furnace";
    }

    @Override
    public void setName(String name) {
        if (name == null || name.equals("")) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag.putString("CustomName", name);
    }

    @Override
    public boolean hasName() {
        return this.namedTag.contains("CustomName");
    }

    public boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    public void setTransferCooldown(int transferCooldown) {
        this.transferCooldown = transferCooldown;
    }

    @Override
    public int getSize() {
        return 3;
    }

    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = this.namedTag.getList("Items", CompoundTag.class);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getByte("Slot") == index) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Item getItem(int index) {
        int i = this.getSlotIndex(index);
        if (i < 0) {
            return new ItemBlock(new BlockAir(), 0, 0);
        } else {
            CompoundTag data = (CompoundTag) this.namedTag.getList("Items").get(i);
            return NBTIO.getItemHelper(data);
        }
    }

    @Override
    public void setItem(
        int index,
        Item item
    ) {
        int i = this.getSlotIndex(index);

        CompoundTag d = NBTIO.putItemHelper(item, index);

        if (item.getId() == Item.AIR || item.getCount() <= 0) {
            if (i >= 0) {
                this.namedTag.getList("Items").getAll().remove(i);
            }
        } else if (i < 0) {
            (this.namedTag.getList("Items", CompoundTag.class)).add(d);
        } else {
            (this.namedTag.getList("Items", CompoundTag.class)).add(i, d);
        }
    }

    @Override
    public void saveNBT() {
        this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        for (int index = 0; index < this.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }

        this.namedTag.putInt("TransferCooldown", this.transferCooldown);
    }

    @Override
    public HopperInventory getInventory() {
        return inventory;
    }

    @Override
    public boolean onUpdate() {
        if (this.closed) {
            return false;
        }

        this.transferCooldown--;

        if (!this.isOnTransferCooldown()) {
            boolean transfer = this.transferItemsOut();
            boolean pickup = this.pickupDroppedItems();

            if (transfer || pickup) {
                //this.setTransferCooldown(8); TODO: maybe we should update hopper every tick if nothing happens?
                this.chunk.setChanged(true);
            }

            this.setTransferCooldown(8);
        }


        return true;
    }

    public boolean pickupDroppedItems() {
        if (this.inventory.isFull()) {
            return false;
        }

        boolean update = false;

        for (Entity entity : this.level.getCollidingEntities(this.pickupArea)) {
            if (!(entity instanceof EntityItem itemEntity)) {
                continue;
            }

            Item item = itemEntity.getItem();

            if (item.getId() == 0 || item.getCount() < 1) {
                continue;
            }

            int originalCount = item.getCount();
            Item[] items = this.inventory.addItem(item);

            if (items.length == 0) {
                entity.close();
                update = true;
                continue;
            }

            if (items[0].getCount() != originalCount) {
                update = true;
            }
        }

        BlockEntity blockEntity = this.level.getBlockEntity(this.up());
        if (blockEntity instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) blockEntity).getInventory();

            for (int i = 0; i < inv.getSize(); i++) {
                Item item = inv.getItem(i);

                if (item.getId() != 0 && item.getCount() > 0) {
                    Item itemToAdd = item.clone();
                    itemToAdd.count = 1;

                    Item[] items = this.inventory.addItem(itemToAdd);

                    if (items.length >= 1) {
                        continue;
                    }

                    item.count--;

                    if (item.count <= 0) {
                        item = Item.get(0);
                    }

                    inv.setItem(i, item);
                    update = true;
                    break;
                }
            }
        }

        //TODO: check for minecart
        return update;
    }

    public boolean transferItemsOut() {
        if (this.inventory.isEmpty()) {
            return false;
        }

        if (!(this.level.getBlockEntity(this.down()) instanceof BlockEntityHopper)) {
            BlockEntity be = this.level.getBlockEntity(this.getSide(BlockFace.fromIndex(this.level.getBlockDataAt(this.getFloorX(), this.getFloorY(), this.getFloorZ()))));

            if (be instanceof InventoryHolder) {
                Inventory inventory = ((InventoryHolder) be).getInventory();

                if (inventory.isFull()) {
                    return false;
                }

                for (int i = 0; i < inventory.getSize(); i++) {
                    Item item = this.inventory.getItem(i);

                    if (item.getId() != 0 && item.getCount() > 0) {
                        Item itemToAdd = item.clone();
                        itemToAdd.setCount(1);

                        Item[] items = inventory.addItem(itemToAdd);

                        if (items.length > 0) {
                            continue;
                        }

                        inventory.sendContents(inventory.getViewers()); //whats wrong?
                        item.count--;
                        this.inventory.setItem(i, item);
                        return true;
                    }
                }
            }

            //TODO: check for minecart
        }

        return false;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = new CompoundTag()
            .putString("id", BlockEntity.HOPPER)
            .putInt("x", (int) this.x)
            .putInt("y", (int) this.y)
            .putInt("z", (int) this.z);

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c;
    }

}
