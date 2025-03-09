package com.github.mille.team.event.player;

import com.github.mille.team.Player;
import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.HandlerList;
import com.github.mille.team.level.Level;
import com.github.mille.team.level.Location;
import com.github.mille.team.level.Position;
import com.github.mille.team.math.Vector3;

public class PlayerTeleportEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private TeleportCause cause;

    private Location from;

    private Location to;

    private PlayerTeleportEvent(Player player) {
        this.player = player;
    }

    public PlayerTeleportEvent(
        Player player,
        Location from,
        Location to,
        TeleportCause cause
    ) {
        this(player);
        this.from = from;
        this.to = to;
        this.cause = cause;
    }

    public PlayerTeleportEvent(
        Player player,
        Vector3 from,
        Vector3 to,
        TeleportCause cause
    ) {
        this(player);
        this.from = vectorToLocation(player.getLevel(), from);
        this.from = vectorToLocation(player.getLevel(), to);
        this.cause = cause;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public TeleportCause getCause() {
        return cause;
    }

    private Location vectorToLocation(
        Level baseLevel,
        Vector3 vector
    ) {
        if (vector instanceof Location) return (Location) vector;
        if (vector instanceof Position) return ((Position) vector).getLocation();
        return new Location(vector.getX(), vector.getY(), vector.getZ(), 0, 0, baseLevel);
    }


    public enum TeleportCause {
        COMMAND,       // For Nukkit tp command only
        PLUGIN,        // Every plugin
        NETHER_PORTAL, // Teleport using Nether portal
        ENDER_PEARL,   // Teleport by ender pearl
        UNKNOWN        // Unknown cause
    }

}
