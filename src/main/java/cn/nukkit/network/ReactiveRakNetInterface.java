package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerCreationEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.raknet.reactive.core.NetworkPacket;
import cn.nukkit.raknet.reactive.core.NetworkStatistics;
import cn.nukkit.raknet.reactive.core.ReactiveUDPChannel;
import cn.nukkit.raknet.reactive.server.ReactiveRakNetSessionManager;
import cn.nukkit.raknet.reactive.server.ReactiveSessionManager;
import cn.nukkit.raknet.reactive.server.ServerStatistics;
import cn.nukkit.raknet.reactive.session.ReactiveSession;
import cn.nukkit.utils.MainLogger;
import io.netty.buffer.Unpooled;
import reactor.core.publisher.Mono;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reactive RakNet interface implementation for Nukkit server integration.
 */
public class ReactiveRakNetInterface implements AdvancedSourceInterface {

    private final Server server;
    private final ReactiveRakNetSessionManager sessionManager;
    private final ReactiveUDPChannel networkChannel;
    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final Map<String, Integer> networkLatency = new ConcurrentHashMap<>();
    private final Map<Integer, String> identifiers = new ConcurrentHashMap<>();
    private final Map<String, Integer> identifiersACK = new ConcurrentHashMap<>();
    private final MainLogger logger;
    
    private Network network;
    private int[] channelCounts = new int[256];
    private boolean shutdown = false;

    public ReactiveRakNetInterface(Server server, String host, int port) {
        this.server = server;
        this.logger = server.getLogger();
        this.networkChannel = new ReactiveUDPChannel(host, port);
        this.sessionManager = new ReactiveRakNetSessionManager(networkChannel);
        
        // Configure session manager
        sessionManager.setServerName(server.getMotd());
        sessionManager.setMaxPlayers(server.getMaxPlayers());
        sessionManager.setSessionTimeout(Duration.ofSeconds(30));
        
        // Subscribe to session events
        sessionManager.sessionEvents()
            .subscribe(this::handleSessionEvent);
        
        // Subscribe to incoming packets
        sessionManager.inbound()
            .subscribe(this::handleIncomingPacket);
        
        logger.info("Reactive RakNet interface initialized on " + host + ":" + port);
    }

    @Override
    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public boolean process() {
        if (shutdown) {
            return false;
        }
        
        // Process statistics and monitoring
        processStatistics();
        
        // Check for query regeneration
        QueryRegenerateEvent queryEvent = server.getQueryInformation();
        if (queryEvent.getTimeout() <= System.currentTimeMillis()) {
            queryEvent.setTimeout(System.currentTimeMillis() + 5000);
        }
        
        return true;
    }

    @Override
    public void closeSession(String identifier, String reason) {
        Player player = players.remove(identifier);
        if (player != null) {
            player.close(player.getLeaveMessage(), reason);
        }
        
        networkLatency.remove(identifier);
        identifiersACK.remove(identifier);
        
        // Remove session from reactive session manager
        sessionManager.removeSession(identifier).subscribe();
    }

    @Override
    public int getNetworkLatency(Player player) {
        return networkLatency.getOrDefault(player.rawHashCode() + "", -1);
    }

    @Override
    public void close(Player player) {
        close(player, "Generic reason");
    }

    @Override
    public void close(Player player, String reason) {
        if (player != null) {
            String identifier = player.rawHashCode() + "";
            closeSession(identifier, reason);
        }
    }

    @Override
    public void shutdown() {
        shutdown = true;
        
        // Shutdown session manager
        sessionManager.stop().block(Duration.ofSeconds(10));
        
        // Disconnect all players
        players.values().forEach(player -> {
            player.close(player.getLeaveMessage(), "Server closed");
        });
        
        players.clear();
        networkLatency.clear();
        identifiers.clear();
        identifiersACK.clear();
        
        logger.info("Reactive RakNet interface shutdown complete");
    }

    @Override
    public void emergencyShutdown() {
        shutdown = true;
        sessionManager.stop().block(Duration.ofSeconds(5));
        players.clear();
        networkLatency.clear();
        identifiers.clear();
        identifiersACK.clear();
        
        logger.info("Reactive RakNet interface emergency shutdown complete");
    }

    @Override
    public void openSession(String identifier, String address, int port, long clientID) {
        InetSocketAddress socketAddress = new InetSocketAddress(address, port);
        
        // Create session in reactive session manager
        sessionManager.createSession(socketAddress, clientID)
            .subscribe(
                session -> {
                    identifiers.put((int) clientID, identifier);
                    
                    // Create player
                    PlayerCreationEvent event = new PlayerCreationEvent(this, Player.class, Player.class, clientID, socketAddress);
                    server.getPluginManager().callEvent(event);
                    
                    Class<? extends Player> clazz = event.getPlayerClass();
                    try {
                        Constructor<? extends Player> constructor = clazz.getConstructor(SourceInterface.class, Long.class, String.class, int.class);
                        Player player = constructor.newInstance(this, clientID, address, port);
                        players.put(identifier, player);
                        server.addPlayer(identifier, player);
                        
                        logger.info("Player " + address + ":" + port + " connected with session " + identifier);
                    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        logger.error("Failed to create player for session " + identifier, e);
                    }
                },
                error -> {
                    logger.error("Failed to create session for " + address + ":" + port, error);
                }
            );
    }

    @Override
    public void handleEncapsulated(String identifier, cn.nukkit.raknet.protocol.EncapsulatedPacket packet, int flags) {
        // This method is called by the old interface, but we handle packets differently in the reactive implementation
        // Convert to reactive format if needed
        if (packet != null && packet.buffer != null) {
            NetworkPacket networkPacket = new NetworkPacket(
                Unpooled.wrappedBuffer(packet.buffer),
                NetworkPacket.PacketType.CUSTOM_PACKET
            );
            
            // Find session and handle packet
            sessionManager.getSession(identifier)
                .flatMap(session -> session.send(networkPacket))
                .subscribe(
                    v -> {}, // Success
                    error -> logger.error("Failed to handle encapsulated packet", error)
                );
        }
    }

    @Override
    public void blockAddress(String address) {
        blockAddress(address, 300);
    }

    @Override
    public void blockAddress(String address, int timeout) {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(address, 0);
            sessionManager.blockAddress(socketAddress, Duration.ofSeconds(timeout)).subscribe();
            logger.info("Blocked address " + address + " for " + timeout + " seconds");
        } catch (Exception e) {
            logger.error("Failed to block address " + address, e);
        }
    }

    @Override
    public void unblockAddress(String address) {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(address, 0);
            sessionManager.unblockAddress(socketAddress).subscribe();
            logger.info("Unblocked address " + address);
        } catch (Exception e) {
            logger.error("Failed to unblock address " + address, e);
        }
    }

    @Override
    public void handleRaw(String address, int port, byte[] payload) {
        // Raw packet handling is now done by the reactive session manager
        // This method is kept for compatibility
    }

    @Override
    public void sendRawPacket(String address, int port, byte[] payload) {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(address, port);
            NetworkPacket packet = new NetworkPacket(
                Unpooled.wrappedBuffer(payload),
                NetworkPacket.PacketType.CUSTOM_PACKET
            );
            
            networkChannel.send(packet, socketAddress).subscribe();
        } catch (Exception e) {
            logger.error("Failed to send raw packet to " + address + ":" + port, e);
        }
    }

    @Override
    public void notifyACK(String identifier, int identifierACK) {
        identifiersACK.put(identifier, identifierACK);
    }

    @Override
    public void setName(String name) {
        sessionManager.setServerName(name);
    }

    public void setPortCheck(boolean value) {
        // Port checking is handled by the reactive implementation
    }

    @Override
    public void handleOption(String name, String value) {
        // Handle configuration options
        switch (name.toLowerCase()) {
            case "name":
                setName(value);
                break;
            case "maxplayers":
                try {
                    int maxPlayers = Integer.parseInt(value);
                    sessionManager.setMaxPlayers(maxPlayers);
                } catch (NumberFormatException e) {
                    logger.warning("Invalid maxplayers value: " + value);
                }
                break;
            case "timeout":
                try {
                    int timeout = Integer.parseInt(value);
                    sessionManager.setSessionTimeout(Duration.ofSeconds(timeout));
                } catch (NumberFormatException e) {
                    logger.warning("Invalid timeout value: " + value);
                }
                break;
        }
    }

    @Override
    public Integer putPacket(Player player, DataPacket packet) {
        return putPacket(player, packet, false);
    }

    @Override
    public Integer putPacket(Player player, DataPacket packet, boolean needACK) {
        return putPacket(player, packet, needACK, false);
    }

    @Override
    public Integer putPacket(Player player, DataPacket packet, boolean needACK, boolean immediate) {
        if (player == null || packet == null) {
            return null;
        }
        
        try {
            packet.protocol = ProtocolInfo.CURRENT_PROTOCOL;
            packet.encode();
            
            byte[] buffer = packet.getBuffer();
            if (buffer == null) {
                return null;
            }
            
            NetworkPacket networkPacket = new NetworkPacket(
                Unpooled.wrappedBuffer(buffer),
                NetworkPacket.PacketType.CUSTOM_PACKET,
                immediate ? 1 : 0,
                needACK
            );
            
            String identifier = player.rawHashCode() + "";
            
            // Send packet through reactive session manager
            sessionManager.getSession(identifier)
                .flatMap(session -> session.send(networkPacket, needACK, immediate ? 1 : 0))
                .subscribe(
                    v -> {}, // Success
                    error -> logger.error("Failed to send packet to player " + player.getName(), error)
                );
            
            return packet.pid();
        } catch (Exception e) {
            logger.error("Error sending packet to player " + player.getName(), e);
            return null;
        }
    }

    /**
     * Starts the reactive RakNet interface
     */
    public void start() {
        sessionManager.start().block(Duration.ofSeconds(30));
        logger.info("Reactive RakNet interface started");
    }

    /**
     * Gets network statistics
     */
    public Mono<NetworkStatistics> getNetworkStatistics() {
        return sessionManager.getNetworkStatistics();
    }

    /**
     * Gets server statistics
     */
    public Mono<ServerStatistics> getServerStatistics() {
        return sessionManager.getServerStatistics();
    }

    private void handleSessionEvent(ReactiveSessionManager.SessionEvent event) {
        switch (event.getType()) {
            case SESSION_CREATED:
                logger.debug("Session created: " + event.getSession().getId());
                break;
            case SESSION_CONNECTED:
                logger.debug("Session connected: " + event.getSession().getId());
                break;
            case SESSION_DISCONNECTED:
                logger.debug("Session disconnected: " + event.getSession().getId() + 
                           (event.getReason() != null ? " (" + event.getReason() + ")" : ""));
                break;
            case SESSION_TIMEOUT:
                logger.debug("Session timeout: " + event.getSession().getId());
                break;
        }
    }

    private void handleIncomingPacket(cn.nukkit.raknet.reactive.core.PacketEvent event) {
        // Handle incoming packets from the reactive layer
        try {
            NetworkPacket packet = event.getPacket();
            byte[] data = packet.getDataArray();
            
            if (data.length > 0) {
                DataPacket dataPacket = getPacket(data);
                if (dataPacket != null) {
                    String identifier = event.getSender().getAddress().getHostAddress() + ":" + event.getSender().getPort();
                    Player player = players.get(identifier);
                    
                    if (player != null) {
                        dataPacket.setBuffer(data, 0);
                        player.handleDataPacket(dataPacket);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error handling incoming packet", e);
        }
    }

    private DataPacket getPacket(byte[] buffer) {
        // Convert byte buffer to DataPacket
        // This is a simplified version - in practice, you'd use the full packet factory
        try {
            if (buffer.length > 0) {
                DataPacket packet = network.getPacket(buffer[0]);
                if (packet != null) {
                    packet.setBuffer(buffer, 1);
                    return packet;
                }
            }
        } catch (Exception e) {
            logger.error("Error creating packet from buffer", e);
        }
        return null;
    }

    private void processStatistics() {
        // Update network latency for players
        players.forEach((identifier, player) -> {
            sessionManager.getSession(identifier)
                .flatMap(ReactiveSession::getStatistics)
                .subscribe(stats -> {
                    networkLatency.put(identifier, (int) stats.getRoundTripTime());
                });
        });
    }
}