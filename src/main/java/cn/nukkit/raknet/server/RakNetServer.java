package cn.nukkit.raknet.server;

import cn.nukkit.Server;
import cn.nukkit.utils.ThreadedLogger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Modern RakNet server implementation using ExecutorService instead of Thread inheritance.
 * 
 * @author MagicDroidX Nukkit Project
 */
public class RakNetServer implements AutoCloseable
{

	protected final int port;

	protected String interfaz;

	protected ThreadedLogger logger;

	protected ConcurrentLinkedQueue<byte[]> externalQueue;

	protected ConcurrentLinkedQueue<byte[]> internalQueue;

	protected final AtomicBoolean shutdown = new AtomicBoolean(false);
	
	protected final ExecutorService executorService;


	public RakNetServer(
		ThreadedLogger logger,
		int port
	)
	{
		this(logger, port, "0.0.0.0");
	}

	public RakNetServer(
		ThreadedLogger logger,
		int port,
		String interfaz
	)
	{
		this.port = port;
		if (port < 1 || port > 65536)
		{
			throw new IllegalArgumentException("Invalid port range");
		}

		this.interfaz = interfaz;
		this.logger = logger;

		this.externalQueue = new ConcurrentLinkedQueue<>();
		this.internalQueue = new ConcurrentLinkedQueue<>();
		this.executorService = Executors.newSingleThreadExecutor(r -> {
			Thread t = new Thread(r, "RakNet-Server-" + port);
			t.setDaemon(true);
			return t;
		});

		this.start();
	}

	public boolean isShutdown()
	{
		return shutdown.get();
	}

	public void shutdown()
	{
		this.shutdown.set(true);
	}

	@Override
	public void close()
	{
		shutdown();
		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
				executorService.shutdownNow();
			}
		} catch (InterruptedException e) {
			executorService.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	public int getPort()
	{
		return port;
	}

	public String getInterface()
	{
		return interfaz;
	}

	public ThreadedLogger getLogger()
	{
		return logger;
	}

	public ConcurrentLinkedQueue<byte[]> getExternalQueue()
	{
		return externalQueue;
	}

	public ConcurrentLinkedQueue<byte[]> getInternalQueue()
	{
		return internalQueue;
	}

	public void pushMainToThreadPacket(byte[] data)
	{
		this.internalQueue.add(data);
	}

	public byte[] readMainToThreadPacket()
	{
		return this.internalQueue.poll();
	}

	public void pushThreadToMainPacket(byte[] data)
	{
		this.externalQueue.add(data);
	}

	public byte[] readThreadToMainPacket()
	{
		return this.externalQueue.poll();
	}

	public CompletableFuture<Void> start()
	{
		return CompletableFuture.runAsync(this::run, executorService);
	}

	protected void run()
	{
		try {
			logger.info("Starting RakNet server on " + interfaz + ":" + port);
			UDPServerSocket socket = new UDPServerSocket(this.getLogger(), port, this.interfaz);
			new SessionManager(this, socket);
		}
		catch (Exception e)
		{
			logger.error("RakNet server error", e);
			if (Server.getInstance() != null) {
				Server.getInstance().getLogger().logException(e);
			}
		}
		finally {
			logger.info("RakNet server stopped");
		}
	}

}
