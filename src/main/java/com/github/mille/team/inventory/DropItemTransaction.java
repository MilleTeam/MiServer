package com.github.mille.team.inventory;

import com.github.mille.team.Player;
import com.github.mille.team.item.Item;

public class DropItemTransaction extends BaseTransaction {

    public DropItemTransaction(Item droppedItem) {
        super(null, -1, droppedItem, Transaction.TYPE_DROP_ITEM);
    }


    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public int getSlot() {
        return -1;
    }

    @Override
    public void sendSlotUpdate(Player source) {
        //Nope
    }

    @Override
    public InOfOutTransaction getChange() {
        return new InOfOutTransaction(this.getTargetItem(), null);
    }

    @Override
    public boolean execute(Player source) {
        Item droppedItem = this.getTargetItem();
        if (!source.getServer().allowInventoryCheats && !source.isCreative()) {
            if (!source.getFloatingInventory().contains(droppedItem)) {
                return false;
            }
            source.getFloatingInventory().removeItem(droppedItem);
        }
        source.dropItem(droppedItem);
        return true;
    }

}
