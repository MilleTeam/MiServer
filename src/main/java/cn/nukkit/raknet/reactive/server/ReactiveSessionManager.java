package cn.nukkit.raknet.reactive.server;

import cn.nukkit.raknet.reactive.core.NetworkPacket;
import cn.nukkit.raknet.reactive.core.NetworkStatistics;
import cn.nukkit.raknet.reactive.core.PacketEvent;
import cn.nukkit.raknet.reactive.session.ReactiveSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Collection;

/**
 * Reactive session manager interface for managing multiple client sessions.
 */
public interface ReactiveSessionManager {

    /**
     * Gets the server ID
     * @return The server ID
     */
    long getServerId();

    /**
     * Gets the server name
     * @return The server name
     */
    String getServerName();

    /**
     * Sets the server name
     * @param name The server name
     */
    void setServerName(String name);

    /**
     * Gets the maximum number of players
     * @return The maximum number of players
     */
    int getMaxPlayers();

    /**
     * Sets the maximum number of players
     * @param maxPlayers The maximum number of players
     */
    void setMaxPlayers(int maxPlayers);

    /**
     * Gets the current number of online players
     * @return The number of online players
     */
    int getOnlinePlayers();

    /**
     * Reactive stream of all incoming packet events from all sessions
     * @return Flux of packet events
     */
    Flux<PacketEvent> inbound();

    /**
     * Reactive stream of session events (connect, disconnect, etc.)
     * @return Flux of session events
     */
    Flux<SessionEvent> sessionEvents();

    /**
     * Creates a new session for the given address
     * @param address The client address
     * @param clientId The client ID
     * @return Mono containing the new session
     */
    Mono<ReactiveSession> createSession(InetSocketAddress address, long clientId);

    /**
     * Gets a session by its ID
     * @param sessionId The session ID
     * @return Mono containing the session, or empty if not found
     */
    Mono<ReactiveSession> getSession(String sessionId);

    /**
     * Gets a session by client address
     * @param address The client address
     * @return Mono containing the session, or empty if not found
     */
    Mono<ReactiveSession> getSessionByAddress(InetSocketAddress address);

    /**
     * Gets all active sessions
     * @return Flux of active sessions
     */
    Flux<ReactiveSession> getActiveSessions();

    /**
     * Removes a session
     * @param sessionId The session ID
     * @return Mono that completes when the session is removed
     */
    Mono<Void> removeSession(String sessionId);

    /**
     * Removes a session by address
     * @param address The client address
     * @return Mono that completes when the session is removed
     */
    Mono<Void> removeSessionByAddress(InetSocketAddress address);

    /**
     * Broadcasts a packet to all active sessions
     * @param packet The packet to broadcast
     * @return Mono that completes when the packet is sent to all sessions
     */
    Mono<Void> broadcast(NetworkPacket packet);

    /**
     * Broadcasts a packet to selected sessions
     * @param packet The packet to broadcast
     * @param sessionIds The session IDs to broadcast to
     * @return Mono that completes when the packet is sent to all selected sessions
     */
    Mono<Void> broadcast(NetworkPacket packet, Collection<String> sessionIds);

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
     * Starts the session manager
     * @return Mono that completes when the session manager is started
     */
    Mono<Void> start();

    /**
     * Stops the session manager
     * @return Mono that completes when the session manager is stopped
     */
    Mono<Void> stop();

    /**
     * Checks if the session manager is running
     * @return True if running, false otherwise
     */
    boolean isRunning();

    /**
     * Gets network statistics
     * @return Mono containing network statistics
     */
    Mono<NetworkStatistics> getNetworkStatistics();

    /**
     * Gets server statistics
     * @return Mono containing server statistics
     */
    Mono<ServerStatistics> getServerStatistics();

    /**
     * Performs cleanup of timed out sessions
     * @return Mono that completes when cleanup is done
     */
    Mono<Void> cleanupTimedOutSessions();

    /**
     * Sets the session timeout duration
     * @param timeout The timeout duration
     */
    void setSessionTimeout(Duration timeout);

    /**
     * Gets the session timeout duration
     * @return The timeout duration
     */
    Duration getSessionTimeout();

    /**
     * Session event types
     */
    enum SessionEventType {
        SESSION_CREATED,
        SESSION_CONNECTED,
        SESSION_DISCONNECTED,
        SESSION_TIMEOUT
    }

    /**
     * Session event data
     */
    class SessionEvent {
        private final EventType type;
        private final String sessionId;
        private final ReactiveSession session;
        private final String reason;

        public SessionEvent(EventType type, String sessionId, ReactiveSession session) {
            this(type, sessionId, session, null);
        }

        public SessionEvent(EventType type, String sessionId, ReactiveSession session, String reason) {
            this.type = type;
            this.sessionId = sessionId;
            this.session = session;
            this.reason = reason;
        }

        public EventType getType() {
            return type;
        }

        public String getSessionId() {
            return sessionId;
        }

        public ReactiveSession getSession() {
            return session;
        }

        public String getReason() {
            return reason;
        }

        @Override
        public String toString() {
            return String.format("SessionEvent{type=%s, sessionId=%s, reason=%s}", 
                               type, sessionId, reason);
        }

        /**
         * Session event types
         */
        public enum EventType {
            SESSION_CREATED,
            SESSION_CONNECTED,
            SESSION_CLOSED,
            SESSION_TIMEOUT
        }
    }
}