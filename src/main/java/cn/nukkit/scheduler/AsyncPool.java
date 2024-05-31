package cn.nukkit.scheduler;

import java.util.Objects;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.nukkit.Server;

import lombok.Getter;

/**
 * @author Nukkit Project Team
 */
@Getter
public class AsyncPool extends ThreadPoolExecutor
{

    private final Server server;

    public AsyncPool(
        Server server,
        int size
    )
    {
        super(size, Integer.MAX_VALUE, 60, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
        this.setThreadFactory(Thread.ofVirtual().name(String.format("Nukkit Virtual Asynchronous Task Handler #%s", this.getPoolSize())).factory());
        this.server = server;
    }

    @Override
    protected void afterExecute(
        Runnable runnable,
        Throwable throwable
    )
    {
        if (Objects.nonNull(throwable))
        {
            server.getLogger().critical("Exception in asynchronous task", throwable);
        }
    }

}
