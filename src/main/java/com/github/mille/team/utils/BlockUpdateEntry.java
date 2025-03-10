package com.github.mille.team.utils;

import com.github.mille.team.block.Block;
import com.github.mille.team.math.Vector3;

/**
 * author: MagicDroidX Nukkit Project
 */
public class BlockUpdateEntry implements Comparable<BlockUpdateEntry> {

    private static long entryID = 0;

    public final Vector3 pos;

    public final Block block;

    public final long id;

    public int priority;

    public long delay;

    public BlockUpdateEntry(
        Vector3 pos,
        Block block
    ) {
        this.pos = pos;
        this.block = block;
        this.id = entryID++;
    }

    public BlockUpdateEntry(
        Vector3 pos,
        Block block,
        long delay,
        int priority
    ) {
        this.id = entryID++;
        this.pos = pos;
        this.priority = priority;
        this.delay = delay;
        this.block = block;
    }

    @Override
    public int compareTo(BlockUpdateEntry entry) {
        return this.delay < entry.delay ? -1 : (this.delay > entry.delay ? 1 : (this.priority != entry.priority ? this.priority - entry.priority : (this.id < entry.id ? -1 : (this.id > entry.id ? 1 : 0))));
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BlockUpdateEntry entry)) {
            return false;
        } else {
            return this.pos.equals(entry.pos) && Block.equals(this.block, entry.block, false);
        }
    }

}