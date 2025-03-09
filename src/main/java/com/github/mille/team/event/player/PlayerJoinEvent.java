package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.lang.TextContainer;

public class PlayerJoinEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    protected TextContainer joinMessage;

    public PlayerJoinEvent(
        Player player,
        TextContainer joinMessage
    ) {
        this.player = player;
        this.joinMessage = joinMessage;
    }

    public PlayerJoinEvent(
        Player player,
        String joinMessage
    ) {
        this.player = player;
        this.joinMessage = new TextContainer(joinMessage);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public TextContainer getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(TextContainer joinMessage) {
        this.joinMessage = joinMessage;
    }

    public void setJoinMessage(String joinMessage) {
        this.setJoinMessage(new TextContainer(joinMessage));
    }

}
