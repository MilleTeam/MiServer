package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.Event;

/**
 * author: MagicDroidX Nukkit Project
 */
public abstract class PlayerEvent extends Event {

    protected Player player;

    public Player getPlayer() {
        return player;
    }

}
