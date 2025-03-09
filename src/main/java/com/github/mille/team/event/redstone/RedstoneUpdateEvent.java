package com.github.mille.team.event.redstone;

import com.github.mille.team.block.Block;
import com.github.mille.team.event.block.BlockUpdateEvent;

/**
 * author: Angelic47 Nukkit Project
 */
public class RedstoneUpdateEvent extends BlockUpdateEvent {

    public RedstoneUpdateEvent(Block source) {
        super(source);
    }

}

