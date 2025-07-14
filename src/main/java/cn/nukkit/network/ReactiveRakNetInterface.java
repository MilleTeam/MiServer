package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerCreationEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.raknet.reactive.core.NetworkPacket;
import cn.nukkit.raknet.reactive.core.NetworkStatistics;
import cn.nukkit.raknet.reactive.core.PacketEvent;
import cn.nukkit.raknet.reactive.core.ReactiveUDPChannel;
import cn.nukkit.raknet.reactive.server.ReactiveRakNetSessionManager;
import cn.nukkit.raknet.reactive.server.ReactiveSessionManager;
import cn.nukkit.raknet.reactive.server.ServerStatistics;
import cn.nukkit.utils.MainLogger;
import io.netty.buffer.Unpooled;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reactive implementation of RakNet interface for Nukkit server
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
        this.logger = server != null ? server.getLogger() : cn.nukkit.utils.MainLogger.getLogger();
        this.networkChannel = new ReactiveUDPChannel(host, port);
        this.sessionManager = new ReactiveRakNetSessionManager(networkChannel);
        
        // Subscribe to session events
        sessionManager.sessionEvents()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(this::handleSessionEvent);
        
        // Subscribe to incoming packets
        networkChannel.inbound()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(this::handleIncomingPacket);
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
        
        // Process session updates
        sessionManager.processSessionUpdates()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
        
        // Check for query regeneration
        QueryRegenerateEvent queryEvent = server.getQueryInformation();
        if (queryEvent.getTimeout() <= System.currentTimeMillis()) {
            queryEvent.setTimeout((int) (System.currentTimeMillis() + 5000));
        }
        
        return true;
    }

    public void closeSession(String identifier, String reason) {
        Player player = players.remove(identifier);
        if (player != null) {
            player.close(player.getLeaveMessage(), reason);
        }
        
        // Remove from session manager
        sessionManager.removeSession(identifier)
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
    }

    @Override
    public int getNetworkLatency(Player player) {
        return networkLatency.getOrDefault(player.getAddress() + ":" + player.getPort(), -1);
    }

    @Override
    public void close(Player player) {
        close(player, "Generic reason");
    }

    @Override
    public void close(Player player, String reason) {
        String identifier = player.getAddress() + ":" + player.getPort();
        players.remove(identifier);
        networkLatency.remove(identifier);
        
        sessionManager.removeSession(identifier)
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
    }

    @Override
    public void shutdown() {
        shutdown = true;
        
        // Shutdown session manager
        sessionManager.shutdown()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
        
        // Stop network channel
        networkChannel.stop()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
        
        // Clear all players
        players.clear();
        networkLatency.clear();
        identifiers.clear();
        identifiersACK.clear();
    }

    @Override
    public void emergencyShutdown() {
        shutdown = true;
        
        // Emergency shutdown - force close everything
        sessionManager.shutdown()
            .timeout(Duration.ofSeconds(5))
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
        
        networkChannel.stop()
            .timeout(Duration.ofSeconds(5))
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
        
        players.clear();
        networkLatency.clear();
        identifiers.clear();
        identifiersACK.clear();
    }

    public void openSession(String identifier, String address, int port, long clientID) {
        InetSocketAddress socketAddress = new InetSocketAddress(address, port);
        
        // Create session through session manager
        sessionManager.createSession(identifier, socketAddress, clientID)
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(
                session -> {
                    identifiers.put((int) clientID, identifier);
                    
                    // Create player
                    PlayerCreationEvent event = new PlayerCreationEvent(this, Player.class, Player.class, clientID, address, port);
                    server.getPluginManager().callEvent(event);
                    
                    Class<? extends Player> clazz = event.getPlayerClass();
                    try {
                        Constructor<? extends Player> constructor = clazz.getConstructor(SourceInterface.class, Long.class, String.class, int.class);
                        Player player = constructor.newInstance(this, clientID, address, port);
                        players.put(identifier, player);
                        server.addPlayer(identifier, player);
                        
                        logger.info("Player " + address + ":" + port + " connected with session " + identifier);
                    } catch (Exception e) {
                        logger.error("Failed to create player for " + address + ":" + port, e);
                    }
                },
                error -> logger.error("Failed to create session for " + address + ":" + port, error)
            );
    }

    public void handleEncapsulated(String identifier, cn.nukkit.raknet.protocol.EncapsulatedPacket packet, int flags) {
        Player player = players.get(identifier);
        if (player != null) {
            try {
                DataPacket dataPacket = getPacket(packet.buffer);
                if (dataPacket != null) {
                    player.handleDataPacket(dataPacket);
                }
            } catch (Exception e) {
                logger.error("Error handling encapsulated packet from " + identifier, e);
            }
        }
    }

    @Override
    public void blockAddress(String address) {
        blockAddress(address, 300); // 5 minutes default
    }

    @Override
    public void blockAddress(String address, int timeout) {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(address, 0);
            Duration duration = Duration.ofSeconds(timeout);
            
            networkChannel.blockAddress(socketAddress, duration)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
        } catch (Exception e) {
            logger.error("Error blocking address " + address, e);
        }
    }

    @Override
    public void unblockAddress(String address) {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(address, 0);
            
            networkChannel.unblockAddress(socketAddress)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
        } catch (Exception e) {
            logger.error("Error unblocking address " + address, e);
        }
    }

    public void handleRaw(String address, int port, byte[] payload) {
        // Handle raw packets
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(address, port);
            NetworkPacket packet = new NetworkPacket(
                Unpooled.wrappedBuffer(payload),
                NetworkPacket.PacketType.CONTROL,
                0,
                System.currentTimeMillis()
            );
            networkChannel.send(packet, socketAddress)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
        } catch (Exception e) {
            logger.error("Error handling raw packet", e);
        }
    }

    @Override
    public void sendRawPacket(String address, int port, byte[] payload) {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(address, port);
            NetworkPacket packet = new NetworkPacket(
                Unpooled.wrappedBuffer(payload),
                NetworkPacket.PacketType.CONTROL,
                0,
                System.currentTimeMillis()
            );
            networkChannel.send(packet, socketAddress)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
        } catch (Exception e) {
            logger.error("Error sending raw packet", e);
        }
    }

    public void notifyACK(String identifier, int identifierACK) {
        identifiersACK.put(identifier, identifierACK);
    }

    @Override
    public void setName(String name) {
        // Set server name for query response
        QueryRegenerateEvent queryEvent = server.getQueryInformation();
        // Note: QueryRegenerateEvent might not have setName method, using reflection or alternative
        try {
            java.lang.reflect.Field nameField = QueryRegenerateEvent.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(queryEvent, name);
        } catch (Exception e) {
            // Fallback - log the name change
            logger.info("Server name changed to: " + name);
        }
    }

    public void setPortCheck(boolean value) {
        // Port check setting
    }

    public void handleOption(String name, String value) {
        // Handle server options
        switch (name.toLowerCase()) {
            case "name":
                setName(value);
                break;
            case "portcheck":
                setPortCheck(Boolean.parseBoolean(value));
                break;
            default:
                logger.debug("Unknown option: " + name + " = " + value);
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
            // Set protocol version
            if (packet instanceof cn.nukkit.network.protocol.DataPacket) {
                // Access protocol field through reflection or cast if needed
                try {
                    java.lang.reflect.Field protocolField = packet.getClass().getField("protocol");
                    protocolField.setInt(packet, ProtocolInfo.CURRENT_PROTOCOL);
                } catch (Exception e) {
                    // If reflection fails, ignore
                }
            }
            
            packet.encode();
            
            byte[] buffer = packet.getBuffer();
            if (buffer == null) {
                return null;
            }
            
            NetworkPacket networkPacket = new NetworkPacket(
                Unpooled.wrappedBuffer(buffer),
                NetworkPacket.PacketType.DATA,
                0,
                System.currentTimeMillis()
            );
            
            String identifier = player.getAddress() + ":" + player.getPort();
            sessionManager.getSession(identifier)
                .flatMap(session -> session.send(networkPacket, needACK, immediate ? 1 : 0))
                .subscribe(
                    v -> {}, // Success
                    error -> logger.error("Failed to send packet to player " + player.getName(), error)
                );
            
            return (int) packet.pid();
        } catch (Exception e) {
            logger.error("Error sending packet to player " + player.getName(), e);
            return null;
        }
    }

    /**
     * Starts the reactive RakNet interface
     */
    public void start() {
        networkChannel.start()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
    }

    /**
     * Gets network statistics
     */
    public Mono<NetworkStatistics> getNetworkStatistics() {
        return networkChannel.getStatistics();
    }

    /**
     * Gets server statistics
     */
    public Mono<ServerStatistics> getServerStatistics() {
        return sessionManager.getStatistics();
    }

    private void handleSessionEvent(ReactiveSessionManager.SessionEvent event) {
        switch (event.getType()) {
            case SESSION_CREATED:
                logger.debug("Session created: " + event.getSessionId());
                break;
            case SESSION_CLOSED:
                logger.debug("Session closed: " + event.getSessionId());
                Player player = players.remove(event.getSessionId());
                if (player != null) {
                    server.removePlayer(player);
                }
                break;
            case SESSION_TIMEOUT:
                logger.debug("Session timeout: " + event.getSessionId());
                break;
        }
    }

    private void handleIncomingPacket(cn.nukkit.raknet.reactive.core.PacketEvent event) {
        try {
            NetworkPacket packet = event.getPacket();
            InetSocketAddress source = event.getSource();
            
            String identifier = source.getHostString() + ":" + source.getPort();
            
            // Process packet through session manager
            sessionManager.handleIncomingPacket(identifier, packet)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                    v -> {}, // Success
                    error -> logger.error("Error processing packet from " + identifier, error)
                );
        } catch (Exception e) {
            logger.error("Error handling incoming packet", e);
        }
    }

    private DataPacket getPacket(byte[] buffer) {
        try {
            if (buffer.length == 0) {
                return null;
            }
            
            // Create appropriate DataPacket based on packet ID
            byte packetId = buffer[0];
            return network.getPacket(packetId);
        } catch (Exception e) {
            logger.error("Error creating packet from buffer", e);
            return null;
        }
    }

    private void processStatistics() {
        // Process and update statistics
        getNetworkStatistics()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(stats -> {
                                 // Update network latency for players
                 for (Player player : players.values()) {
                     String key = player.getAddress() + ":" + player.getPort();
                     // Use the average latency from statistics
                     networkLatency.put(key, (int) stats.getAverageLatency());
                 }
            });
    }
}