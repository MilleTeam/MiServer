package cn.nukkit.raknet.reactive.server;

import cn.nukkit.raknet.reactive.core.NetworkPacket;
import cn.nukkit.raknet.reactive.core.NetworkStatistics;
import cn.nukkit.raknet.reactive.core.PacketEvent;
import cn.nukkit.raknet.reactive.core.ReactiveNetworkChannel;
import cn.nukkit.raknet.reactive.session.ReactiveRakNetSession;
import cn.nukkit.raknet.reactive.session.ReactiveSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Concrete implementation of ReactiveSessionManager for RakNet protocol.
 */
public class ReactiveRakNetSessionManager implements ReactiveSessionManager {

    private final long serverId;
    private final ReactiveNetworkChannel networkChannel;
    private final ServerStatistics serverStatistics;
    private final ConcurrentHashMap<String, ReactiveSession> sessions;
    private final ConcurrentHashMap<InetSocketAddress, String> addressToSessionId;
    private final AtomicBoolean isRunning;
    private final AtomicInteger maxPlayers;
    private final Sinks.Many<PacketEvent> inboundSink;
    private final Flux<PacketEvent> inboundFlux;
    private final Sinks.Many<SessionEvent> sessionEventSink;
    private final Flux<SessionEvent> sessionEventFlux;
    private final AtomicLong sessionIdGenerator;
    
    private volatile Duration sessionTimeout = Duration.ofMinutes(5);
    private volatile String serverName = "Reactive RakNet Server";

    public ReactiveRakNetSessionManager(ReactiveNetworkChannel networkChannel) {
        this.serverId = System.currentTimeMillis();
        this.networkChannel = networkChannel;
        this.serverStatistics = new ServerStatistics();
        this.sessions = new ConcurrentHashMap<>();
        this.addressToSessionId = new ConcurrentHashMap<>();
        this.isRunning = new AtomicBoolean(false);
        this.maxPlayers = new AtomicInteger(100);
        this.inboundSink = Sinks.many().multicast().onBackpressureBuffer();
        this.inboundFlux = inboundSink.asFlux()
            .subscribeOn(Schedulers.boundedElastic())
            .publishOn(Schedulers.parallel());
        this.sessionEventSink = Sinks.many().multicast().onBackpressureBuffer();
        this.sessionEventFlux = sessionEventSink.asFlux()
            .subscribeOn(Schedulers.boundedElastic())
            .publishOn(Schedulers.parallel());
        this.sessionIdGenerator = new AtomicLong(0);
        
        // Initialize server statistics
        serverStatistics.setServerName(serverName);
        serverStatistics.setMaxPlayers(maxPlayers.get());
    }

    @Override
    public long getServerId() {
        return serverId;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public void setServerName(String name) {
        this.serverName = name;
        serverStatistics.setServerName(name);
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers.get();
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers.set(maxPlayers);
        serverStatistics.setMaxPlayers(maxPlayers);
    }

    @Override
    public int getOnlinePlayers() {
        return (int) sessions.values().stream()
            .filter(session -> session.getState() == ReactiveSession.SessionState.CONNECTED)
            .count();
    }

    @Override
    public Flux<PacketEvent> inbound() {
        return inboundFlux;
    }

    @Override
    public Flux<SessionEvent> sessionEvents() {
        return sessionEventFlux;
    }

    @Override
    public Mono<ReactiveSession> createSession(InetSocketAddress address, long clientId) {
        return Mono.fromCallable(() -> {
            String sessionId = generateSessionId();
            ReactiveRakNetSession session = new ReactiveRakNetSession(
                sessionId,
                address,
                clientId,
                networkChannel,
                sessionTimeout
            );
            
            sessions.put(sessionId, session);
            addressToSessionId.put(address, sessionId);
            
            // Session is ready to use
            // serverStatistics.incrementSessionsCreated();
            
            // Emit session created event
            SessionEvent event = new SessionEvent(SessionEvent.EventType.SESSION_CREATED, sessionId, session);
            sessionEventSink.tryEmitNext(event);
            
            return (ReactiveSession) session;
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Creates a new session with a specific identifier
     */
    public Mono<ReactiveSession> createSession(String identifier, InetSocketAddress address, long clientId) {
        return Mono.fromCallable(() -> {
            ReactiveRakNetSession session = new ReactiveRakNetSession(
                identifier,
                address,
                clientId,
                networkChannel,
                sessionTimeout
            );
            
            sessions.put(identifier, session);
            addressToSessionId.put(address, identifier);
            
            // Session is ready to use
            // serverStatistics.incrementSessionsCreated();
            
            // Emit session created event
            SessionEvent event = new SessionEvent(SessionEvent.EventType.SESSION_CREATED, identifier, session);
            sessionEventSink.tryEmitNext(event);
            
            return (ReactiveSession) session;
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ReactiveSession> getSession(String sessionId) {
        return Mono.fromCallable(() -> sessions.get(sessionId))
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ReactiveSession> getSessionByAddress(InetSocketAddress address) {
        return Mono.fromCallable(() -> {
            String sessionId = addressToSessionId.get(address);
            return sessionId != null ? sessions.get(sessionId) : null;
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<ReactiveSession> getActiveSessions() {
        return Flux.fromIterable(sessions.values())
            .filter(session -> session.getState() != ReactiveSession.SessionState.DISCONNECTED)
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> removeSession(String sessionId) {
        return Mono.fromRunnable(() -> {
            ReactiveSession session = sessions.remove(sessionId);
            if (session != null) {
                addressToSessionId.remove(session.getAddress());
                serverStatistics.decrementCurrentConnections();
                serverStatistics.incrementTotalDisconnections();
                
                // Emit session disconnected event
                sessionEventSink.tryEmitNext(new SessionEvent(SessionEvent.EventType.SESSION_CLOSED, sessionId, session));
            }
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }

    @Override
    public Mono<Void> removeSessionByAddress(InetSocketAddress address) {
        return Mono.fromCallable(() -> addressToSessionId.get(address))
            .flatMap(sessionId -> {
                if (sessionId != null) {
                    return removeSession(sessionId);
                }
                return Mono.empty();
            })
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> broadcast(NetworkPacket packet) {
        return getActiveSessions()
            .flatMap(session -> session.send(packet.copy()))
            .then()
            .subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Void> broadcast(NetworkPacket packet, Collection<String> sessionIds) {
        return Flux.fromIterable(sessionIds)
            .flatMap(this::getSession)
            .flatMap(session -> session.send(packet.copy()))
            .then()
            .subscribeOn(Schedulers.parallel());
    }

    @Override
    public Mono<Void> blockAddress(InetSocketAddress address, Duration duration) {
        return networkChannel.blockAddress(address, duration)
            .doOnSuccess(v -> serverStatistics.incrementBlockedAddresses());
    }

    @Override
    public Mono<Void> unblockAddress(InetSocketAddress address) {
        return networkChannel.unblockAddress(address)
            .doOnSuccess(v -> serverStatistics.decrementBlockedAddresses());
    }

    @Override
    public Mono<Boolean> isBlocked(InetSocketAddress address) {
        return networkChannel.isBlocked(address);
    }

    @Override
    public Mono<Void> start() {
        return Mono.fromCallable(() -> {
            if (isRunning.compareAndSet(false, true)) {
                // Start network channel
                networkChannel.start().block();
                
                // Subscribe to incoming packets
                networkChannel.inbound()
                    .subscribe(this::handleIncomingPacket);
                
                // Start periodic cleanup task
                Flux.interval(Duration.ofSeconds(30))
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe(tick -> cleanupTimedOutSessions().subscribe());
                
                // Start statistics update task
                Flux.interval(Duration.ofMinutes(1))
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe(tick -> updateStatistics());
            }
            return null;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }

    @Override
    public Mono<Void> stop() {
        return Mono.fromRunnable(() -> {
            if (isRunning.compareAndSet(true, false)) {
                // Disconnect all sessions
                sessions.values().forEach(session -> {
                    session.disconnect("Server shutting down").subscribe();
                });
                
                // Clear sessions
                sessions.clear();
                addressToSessionId.clear();
                
                // Stop network channel
                networkChannel.stop().block();
                
                // Complete sinks
                inboundSink.tryEmitComplete();
                sessionEventSink.tryEmitComplete();
            }
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }

    /**
     * Handles incoming packets for a specific session
     */
    public Mono<Void> handleIncomingPacket(String sessionId, NetworkPacket packet) {
        return getSession(sessionId)
            .flatMap(session -> {
                // Create a PacketEvent from the NetworkPacket
                PacketEvent event = new PacketEvent(packet, session.getAddress(), packet.getSequenceNumber());
                return session.handlePacket(event);
            })
            .switchIfEmpty(Mono.empty());
    }

    /**
     * Processes session updates
     */
    public Mono<Void> processSessionUpdates() {
        return Mono.fromRunnable(() -> {
            // Update statistics
            updateStatistics();
            
            // Clean up timed out sessions
            cleanupTimedOutSessions().subscribe();
        });
    }

    /**
     * Shutdown the session manager
     */
    public Mono<Void> shutdown() {
        return stop();
    }

    /**
     * Gets server statistics
     */
    public Mono<ServerStatistics> getStatistics() {
        return getServerStatistics();
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public Mono<NetworkStatistics> getNetworkStatistics() {
        return networkChannel.getStatistics();
    }

    @Override
    public Mono<ServerStatistics> getServerStatistics() {
        return Mono.just(serverStatistics);
    }

    @Override
    public Mono<Void> cleanupTimedOutSessions() {
        return Flux.fromIterable(sessions.values())
            .filterWhen(ReactiveSession::isTimedOut)
            .flatMap(session -> {
                serverStatistics.incrementTimeoutDisconnections();
                sessionEventSink.tryEmitNext(new SessionEvent(SessionEvent.EventType.SESSION_TIMEOUT, session.getId(), session, "Session timed out"));
                return removeSession(session.getId());
            })
            .then()
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public void setSessionTimeout(Duration timeout) {
        this.sessionTimeout = timeout;
    }

    @Override
    public Duration getSessionTimeout() {
        return sessionTimeout;
    }

    private void handleIncomingPacket(PacketEvent event) {
        try {
            InetSocketAddress sender = event.getSender();
            
            // Get or create session for this address
            getSessionByAddress(sender)
                .switchIfEmpty(createSession(sender, System.currentTimeMillis()))
                .flatMap(session -> session.handlePacket(event))
                .subscribe(
                    v -> {}, // Success
                    error -> {
                        serverStatistics.incrementTotalErrors();
                        System.err.println("Error handling packet: " + error.getMessage());
                    }
                );
                
        } catch (Exception e) {
            serverStatistics.incrementTotalErrors();
            System.err.println("Error processing incoming packet: " + e.getMessage());
        }
    }

    private void updateStatistics() {
        // Calculate average session duration
        long totalDuration = sessions.values().stream()
            .mapToLong(session -> Duration.between(session.getCreationTime(), session.getLastActivity()).toMillis())
            .sum();
        
        long sessionCount = sessions.size();
        if (sessionCount > 0) {
            serverStatistics.setAverageSessionDuration(totalDuration / sessionCount);
        }
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}