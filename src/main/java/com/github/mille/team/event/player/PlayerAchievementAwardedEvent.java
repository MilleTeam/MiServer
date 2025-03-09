package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;

public class PlayerAchievementAwardedEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    protected final String achievement;

    public PlayerAchievementAwardedEvent(
        Player player,
        String achievementId
    ) {
        this.player = player;
        this.achievement = achievementId;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public String getAchievement() {
        return this.achievement;
    }

}
