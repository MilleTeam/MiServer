package com.github.mille.team.event.block;

import com.github.mille.team.Player;
import com.github.mille.team.block.Block;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

/**
 * author: MagicDroidX Nukkit Project
 */
public class SignChangeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    private String[] lines = new String[4];

    public SignChangeEvent(
        Block block,
        Player player,
        String[] lines
    ) {
        super(block);
        this.player = player;
        this.lines = lines;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public String[] getLines() {
        return lines;
    }

    public String getLine(int index) {
        return this.lines[index];
    }

    public void setLine(
        int index,
        String line
    ) {
        this.lines[index] = line;
    }

}
