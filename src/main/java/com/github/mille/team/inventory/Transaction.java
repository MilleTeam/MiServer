package com.github.mille.team.inventory;

import com.github.mille.team.Player;
import com.github.mille.team.item.Item;

/**
 * author: MagicDroidX Nukkit Project
 */
public interface Transaction {

    int TYPE_NORMAL = 0;

    int TYPE_DROP_ITEM = 1;

    Inventory getInventory();

    void addFailure();

    int getFailures();

    boolean succeeded();

    void setSuccess();

    void setSuccess(boolean value);

    int getSlot();

    Item getTargetItem();

    void setTargetItem(Item item);

    Item getSourceItem();

    void sendSlotUpdate(Player source);

    int getTransactionType();

    long getCreationTime();

    boolean execute(Player source);

}
