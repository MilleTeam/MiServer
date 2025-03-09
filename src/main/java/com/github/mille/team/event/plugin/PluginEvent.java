package com.github.mille.team.event.plugin;

import com.github.mille.team.event.Event;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.plugin.Plugin;

/**
 * author: MagicDroidX Nukkit Project
 */
public class PluginEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Plugin plugin;

    public PluginEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Plugin getPlugin() {
        return plugin;
    }

}
