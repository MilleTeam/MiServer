package com.github.mille.team.plugin;

import com.github.mille.team.event.Event;
import com.github.mille.team.event.Listener;
import com.github.mille.team.utils.EventException;

/**
 * author: iNevet Nukkit Project
 */
public interface EventExecutor {

    void execute(
        Listener listener,
        Event event
    ) throws EventException;

}
