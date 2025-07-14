package cn.nukkit.raknet.reactive.core;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.udp.UdpInbound;
import reactor.netty.udp.UdpOutbound;

import java.net.InetSocketAddress;
import java.time.Duration;

/**
 * Core reactive network channel abstraction for RakNet implementation.
 * Provides reactive streams for handling UDP network operations.
 */
public interface ReactiveNetworkChannel {

    /**
     * Reactive stream of incoming packets
     * @return Flux of incoming packet events
     */
    Flux<PacketEvent> inbound();

    /**
     * Sends a packet to the specified address
     * @param packet The packet to send
     * @param address The destination address
     * @return Mono that completes when the packet is sent
     */
    Mono<Void> send(NetworkPacket packet, InetSocketAddress address);

    /**
     * Sends a packet with retry logic
     * @param packet The packet to send
     * @param address The destination address
     * @param maxRetries Maximum number of retries
     * @param retryDelay Delay between retries
     * @return Mono that completes when the packet is sent or retries are exhausted
     */
    Mono<Void> sendWithRetry(NetworkPacket packet, InetSocketAddress address, 
                           int maxRetries, Duration retryDelay);

    /**
     * Blocks an address for the specified duration
     * @param address The address to block
     * @param duration The duration to block
     * @return Mono that completes when the address is blocked
     */
    Mono<Void> blockAddress(InetSocketAddress address, Duration duration);

    /**
     * Unblocks an address
     * @param address The address to unblock
     * @return Mono that completes when the address is unblocked
     */
    Mono<Void> unblockAddress(InetSocketAddress address);

    /**
     * Checks if an address is blocked
     * @param address The address to check
     * @return Mono that emits true if blocked, false otherwise
     */
    Mono<Boolean> isBlocked(InetSocketAddress address);

    /**
     * Starts the reactive network channel
     * @return Mono that completes when the channel is started
     */
    Mono<Void> start();

    /**
     * Stops the reactive network channel
     * @return Mono that completes when the channel is stopped
     */
    Mono<Void> stop();

    /**
     * Gets the local address the channel is bound to
     * @return The local address
     */
    InetSocketAddress getLocalAddress();

    /**
     * Gets network statistics
     * @return Mono containing network statistics
     */
    Mono<NetworkStatistics> getStatistics();
}