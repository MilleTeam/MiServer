package com.github.mille.team.plugin;

import com.github.mille.team.event.Cancellable;
import com.github.mille.team.event.Event;
import com.github.mille.team.event.EventPriority;
import com.github.mille.team.event.Listener;
import com.github.mille.team.utils.EventException;

/**
 * author: MagicDroidX Nukkit Project
 */
public class RegisteredListener {

    private final Listener listener;

    private final EventPriority priority;

    private final Plugin plugin;

    private final EventExecutor executor;

    private final boolean ignoreCancelled;

    public RegisteredListener(
        Listener listener,
        EventExecutor executor,
        EventPriority priority,
        Plugin plugin,
        boolean ignoreCancelled
    ) {
        this.listener = listener;
        this.priority = priority;
        this.plugin = plugin;
        this.executor = executor;
        this.ignoreCancelled = ignoreCancelled;
    }

    public Listener getListener() {
        return listener;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public void callEvent(Event event) throws EventException {
        if (event instanceof Cancellable) {
            if (event.isCancelled() && isIgnoringCancelled()) {
                return;
            }
        }
        executor.execute(listener, event);
    }

    public boolean isIgnoringCancelled() {
        return ignoreCancelled;
    }

}
