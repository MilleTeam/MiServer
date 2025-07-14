package cn.nukkit.raknet.reactive.session;

import cn.nukkit.raknet.reactive.core.NetworkPacket;
import cn.nukkit.raknet.reactive.core.PacketEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;

/**
 * Reactive session interface for managing individual client connections.
 */
public interface ReactiveSession {

    /**
     * Gets the unique session identifier
     * @return The session identifier
     */
    String getId();

    /**
     * Gets the client address
     * @return The client address
     */
    InetSocketAddress getAddress();

    /**
     * Gets the client ID
     * @return The client ID
     */
    long getClientId();

    /**
     * Gets the current session state
     * @return The session state
     */
    SessionState getState();

    /**
     * Gets the session creation time
     * @return The creation time
     */
    Instant getCreationTime();

    /**
     * Gets the last activity time
     * @return The last activity time
     */
    Instant getLastActivity();

    /**
     * Gets the session timeout duration
     * @return The timeout duration
     */
    Duration getTimeout();

    /**
     * Gets the MTU size for this session
     * @return The MTU size
     */
    int getMtuSize();

    /**
     * Sets the MTU size for this session
     * @param mtuSize The MTU size
     */
    void setMtuSize(int mtuSize);

    /**
     * Reactive stream of incoming packets for this session
     * @return Flux of packet events
     */
    Flux<PacketEvent> inbound();

    /**
     * Sends a packet to the client
     * @param packet The packet to send
     * @return Mono that completes when the packet is sent
     */
    Mono<Void> send(NetworkPacket packet);

    /**
     * Sends a packet with reliability guarantees
     * @param packet The packet to send
     * @param reliable Whether the packet should be reliable
     * @param priority The packet priority
     * @return Mono that completes when the packet is sent
     */
    Mono<Void> send(NetworkPacket packet, boolean reliable, int priority);

    /**
     * Handles an incoming packet
     * @param event The packet event
     * @return Mono that completes when the packet is handled
     */
    Mono<Void> handlePacket(PacketEvent event);

    /**
     * Sends an ACK for the given packet
     * @param sequenceNumber The sequence number to acknowledge
     * @return Mono that completes when the ACK is sent
     */
    Mono<Void> sendAck(long sequenceNumber);

    /**
     * Sends a NACK for the given packet
     * @param sequenceNumber The sequence number to negative acknowledge
     * @return Mono that completes when the NACK is sent
     */
    Mono<Void> sendNack(long sequenceNumber);

    /**
     * Checks if the session is active
     * @return Mono that emits true if active, false otherwise
     */
    Mono<Boolean> isActive();

    /**
     * Checks if the session is timed out
     * @return Mono that emits true if timed out, false otherwise
     */
    Mono<Boolean> isTimedOut();

    /**
     * Updates the session activity timestamp
     * @return Mono that completes when the timestamp is updated
     */
    Mono<Void> updateActivity();

    /**
     * Disconnects the session
     * @return Mono that completes when the session is disconnected
     */
    Mono<Void> disconnect();

    /**
     * Disconnects the session with a reason
     * @param reason The disconnect reason
     * @return Mono that completes when the session is disconnected
     */
    Mono<Void> disconnect(String reason);

    /**
     * Gets session statistics
     * @return Mono containing session statistics
     */
    Mono<SessionStatistics> getStatistics();

    /**
     * Session state enumeration
     */
    enum SessionState {
        UNCONNECTED,
        CONNECTING_1,
        CONNECTING_2,
        CONNECTED,
        DISCONNECTING,
        DISCONNECTED
    }
}