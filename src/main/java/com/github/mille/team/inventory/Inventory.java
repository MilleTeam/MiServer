package com.github.mille.team.inventory;

import com.github.mille.team.Player;
import com.github.mille.team.item.Item;
import com.github.mille.team.level.Level;
import com.github.mille.team.math.Vector3;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * author: MagicDroidX Nukkit Project
 */
public interface Inventory {

    int MAX_STACK = 64;

    void dropContents(
        Level level,
        Vector3 position
    );

    int getSize();

    int getMaxStackSize();

    void setMaxStackSize(int size);

    String getName();

    String getTitle();

    Item getItem(int index);

    boolean setItem(
        int index,
        Item item,
        boolean send
    );

    boolean setItem(
        int index,
        Item item
    );

    Item[] addItem(Item... slots);

    boolean canAddItem(Item item);

    Item[] removeItem(Item... slots);

    Map<Integer, Item> getContents();

    void setContents(Map<Integer, Item> items);

    void setContents(
        Map<Integer, Item> items,
        boolean send
    );

    void sendContents(Player player);

    void sendContents(Player[] players);

    void sendContents(Collection<Player> players);

    void sendSlot(
        int index,
        Player player
    );

    void sendSlot(
        int index,
        Player[] players
    );

    void sendSlot(
        int index,
        Collection<Player> players
    );

    boolean contains(Item item);

    boolean slotContains(
        int slot,
        Item item,
        boolean matchCount
    );

    boolean slotContains(
        int slot,
        Item item
    );

    Map<Integer, Item> all(Item item);

    int first(Item item);

    int firstEmpty(Item item);

    void remove(
        Item item,
        boolean send
    );

    void remove(Item item);

    boolean clear(
        int index,
        boolean send
    );

    boolean clear(int index);

    void clearAll(boolean send);

    void clearAll();

    boolean isFull();

    boolean isEmpty();

    Set<Player> getViewers();

    InventoryType getType();

    InventoryHolder getHolder();

    void onOpen(Player who);

    boolean open(Player who);

    void close(Player who);

    void onClose(Player who);

    boolean processSlotChange(Transaction transaction);

    void onSlotChange(
        int index,
        Item before,
        boolean send
    );

}
