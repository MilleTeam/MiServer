package cn.nukkit.raknet.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.ChannelOption;
import io.netty.util.concurrent.Future;

import cn.nukkit.utils.ThreadedLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Modernized UDP server socket with improved error handling and resource management.
 * 
 * @author MagicDroidX Nukkit Project
 */
public class UDPServerSocket extends ChannelInboundHandlerAdapter implements AutoCloseable
{

	protected final ThreadedLogger logger;

	protected Bootstrap bootstrap;

	protected EventLoopGroup group;

	protected Channel channel;

	protected final ConcurrentLinkedQueue<DatagramPacket> packets = new ConcurrentLinkedQueue<>();
	
	protected final AtomicBoolean closed = new AtomicBoolean(false);

	public UDPServerSocket(ThreadedLogger logger)
	{
		this(logger, 19132, "0.0.0.0");
	}

	public UDPServerSocket(
		ThreadedLogger logger,
		int port
	)
	{
		this(logger, port, "0.0.0.0");
	}

	public UDPServerSocket(
		ThreadedLogger logger,
		int port,
		String interfaz
	)
	{
		this.logger = logger;
		try
		{
			bootstrap = new Bootstrap();
			group = new NioEventLoopGroup();
			bootstrap
				.group(group)
				.channel(NioDatagramChannel.class)
				.option(ChannelOption.SO_BROADCAST, true)
				.option(ChannelOption.SO_RCVBUF, 1024 * 1024) // 1MB receive buffer
				.option(ChannelOption.SO_SNDBUF, 1024 * 1024) // 1MB send buffer
				.handler(this);
			channel = bootstrap.bind(interfaz, port).sync().channel();
			logger.info("UDP server bound to " + interfaz + ":" + port);
		}
		catch (Exception e)
		{
			this.logger.critical("**** FAILED TO BIND TO " + interfaz + ":" + port + "!");
			this.logger.critical("Perhaps a server is already running on that port?");
			this.logger.critical("Error: " + e.getMessage(), e);
			System.exit(1);
		}
	}

	@Override
	public void close()
	{
		if (closed.compareAndSet(false, true)) {
			logger.info("Closing UDP server socket");
			try {
				if (channel != null) {
					channel.close().sync();
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.warning("Interrupted while closing channel");
			} catch (Exception e) {
				logger.warning("Error closing channel", e);
			}
			
			try {
				if (group != null) {
					Future<?> future = group.shutdownGracefully(0, 5, TimeUnit.SECONDS);
					future.sync();
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.warning("Interrupted while shutting down event loop group");
			} catch (Exception e) {
				logger.warning("Error shutting down event loop group", e);
			}
			
			// Clear packet queue
			packets.clear();
		}
	}

	public void clearPacketQueue()
	{
		this.packets.clear();
	}

	public DatagramPacket readPacket() throws IOException
	{
		if (closed.get()) {
			return null;
		}
		return this.packets.poll();
	}

	public int writePacket(
		byte[] data,
		String dest,
		int port
	) throws IOException
	{
		if (closed.get()) {
			throw new IOException("Socket is closed");
		}
		return this.writePacket(data, new InetSocketAddress(dest, port));
	}

	public int writePacket(
		byte[] data,
		InetSocketAddress dest
	) throws IOException
	{
		if (closed.get()) {
			throw new IOException("Socket is closed");
		}
		
		try {
			channel.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(data), dest));
			return data.length;
		} catch (Exception e) {
			logger.warning("Error writing packet to " + dest, e);
			throw new IOException("Failed to write packet", e);
		}
	}

	@Override
	public void channelRead(
		ChannelHandlerContext ctx,
		Object msg
	) throws Exception
	{
		if (!closed.get() && msg instanceof DatagramPacket) {
			this.packets.add((DatagramPacket) msg);
		}
	}

	@Override
	public void exceptionCaught(
		ChannelHandlerContext ctx,
		Throwable cause
	)
	{
		if (!closed.get()) {
			this.logger.warning("UDP socket exception: " + cause.getMessage(), cause);
		}
	}
	
	/**
	 * Check if the socket is closed.
	 */
	public boolean isClosed() {
		return closed.get();
	}
	
	/**
	 * Get the number of packets in the queue.
	 */
	public int getPacketQueueSize() {
		return packets.size();
	}

}
