package com.github.mille.team.event.player;

import com.github.mille.team.AdventureSettings;
import com.github.mille.team.Player;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

public class PlayerGameModeChangeEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    protected final int gamemode;

    protected AdventureSettings newAdventureSettings;

    public PlayerGameModeChangeEvent(
        Player player,
        int newGameMode,
        AdventureSettings newAdventureSettings
    ) {
        this.player = player;
        this.gamemode = newGameMode;
        this.newAdventureSettings = newAdventureSettings;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public int getNewGamemode() {
        return gamemode;
    }

    public AdventureSettings getNewAdventureSettings() {
        return newAdventureSettings;
    }

    public void setNewAdventureSettings(AdventureSettings newAdventureSettings) {
        this.newAdventureSettings = newAdventureSettings;
    }

}
