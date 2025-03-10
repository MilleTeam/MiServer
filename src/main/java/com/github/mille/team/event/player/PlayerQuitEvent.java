package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.lang.TextContainer;

public class PlayerQuitEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    protected TextContainer quitMessage;

    protected boolean autoSave = true;

    protected String reason;

    public PlayerQuitEvent(
        Player player,
        TextContainer quitMessage,
        String reason
    ) {
        this(player, quitMessage, true, reason);
    }

    public PlayerQuitEvent(
        Player player,
        TextContainer quitMessage
    ) {
        this(player, quitMessage, true);
    }

    public PlayerQuitEvent(
        Player player,
        String quitMessage,
        String reason
    ) {
        this(player, quitMessage, true, reason);
    }

    public PlayerQuitEvent(
        Player player,
        String quitMessage
    ) {
        this(player, quitMessage, true);
    }

    public PlayerQuitEvent(
        Player player,
        String quitMessage,
        boolean autoSave,
        String reason
    ) {
        this(player, new TextContainer(quitMessage), autoSave, reason);
    }

    public PlayerQuitEvent(
        Player player,
        String quitMessage,
        boolean autoSave
    ) {
        this(player, new TextContainer(quitMessage), autoSave);
    }

    public PlayerQuitEvent(
        Player player,
        TextContainer quitMessage,
        boolean autoSave
    ) {
        this(player, quitMessage, autoSave, "No reason");
    }

    public PlayerQuitEvent(
        Player player,
        TextContainer quitMessage,
        boolean autoSave,
        String reason
    ) {
        this.player = player;
        this.quitMessage = quitMessage;
        this.autoSave = autoSave;
        this.reason = reason;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public TextContainer getQuitMessage() {
        return quitMessage;
    }

    public void setQuitMessage(TextContainer quitMessage) {
        this.quitMessage = quitMessage;
    }

    public void setQuitMessage(String quitMessage) {
        this.setQuitMessage(new TextContainer(quitMessage));
    }

    public boolean getAutoSave() {
        return this.autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public void setAutoSave() {
        this.setAutoSave(true);
    }

    public String getReason() {
        return reason;
    }

}
