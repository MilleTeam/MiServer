package cn.nukkit.raknet.reactive.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.netty.udp.UdpClient;
import reactor.netty.udp.UdpServer;
import reactor.util.retry.Retry;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Concrete implementation of ReactiveNetworkChannel using Reactor Netty for UDP operations.
 */
public class ReactiveUDPChannel implements ReactiveNetworkChannel {

    private final InetSocketAddress bindAddress;
    private final NetworkStatistics statistics;
    private final ConcurrentHashMap<InetSocketAddress, Long> blockedAddresses;
    private final AtomicLong sequenceNumber;
    private final AtomicBoolean isRunning;
    private final Sinks.Many<PacketEvent> inboundSink;
    private final Flux<PacketEvent> inboundFlux;
    
    private UdpServer server;
    private UdpClient client;
    
    public ReactiveUDPChannel(String host, int port) {
        this.bindAddress = new InetSocketAddress(host, port);
        this.statistics = new NetworkStatistics();
        this.blockedAddresses = new ConcurrentHashMap<>();
        this.sequenceNumber = new AtomicLong(0);
        this.isRunning = new AtomicBoolean(false);
        this.inboundSink = Sinks.many().multicast().onBackpressureBuffer();
        this.inboundFlux = inboundSink.asFlux()
            .subscribeOn(Schedulers.boundedElastic())
            .publishOn(Schedulers.parallel());
    }

    @Override
    public Flux<PacketEvent> inbound() {
        return inboundFlux;
    }

    @Override
    public Mono<Void> send(NetworkPacket packet, InetSocketAddress address) {
        return Mono.fromCallable(() -> {
            if (!isRunning.get()) {
                throw new IllegalStateException("Channel is not running");
            }
            return packet;
        })
        .flatMap(p -> isBlocked(address)
            .filter(blocked -> !blocked)
            .switchIfEmpty(Mono.error(new RuntimeException("Address is blocked: " + address)))
            .then(Mono.fromCallable(() -> {
                ByteBuf buffer = p.getData();
                DatagramPacket datagramPacket = new DatagramPacket(buffer, address);
                
                statistics.incrementPacketsSent();
                statistics.addBytesSent(buffer.readableBytes());
                
                return datagramPacket;
            }))
        )
        .flatMap(datagramPacket -> {
            if (client == null) {
                client = UdpClient.create();
            }
            return client.send(Mono.just(datagramPacket));
        })
        .then()
        .doOnError(error -> {
            statistics.incrementPacketsDropped();
        });
    }

    @Override
    public Mono<Void> sendWithRetry(NetworkPacket packet, InetSocketAddress address, 
                                  int maxRetries, Duration retryDelay) {
        return send(packet, address)
            .retryWhen(Retry.fixedDelay(maxRetries, retryDelay)
                .filter(throwable -> !(throwable instanceof RuntimeException) || 
                       !throwable.getMessage().startsWith("Address is blocked")));
    }

    @Override
    public Mono<Void> blockAddress(InetSocketAddress address, Duration duration) {
        return Mono.fromRunnable(() -> {
            long unblockTime = System.currentTimeMillis() + duration.toMillis();
            blockedAddresses.put(address, unblockTime);
            statistics.incrementBlockedAddresses();
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }

    @Override
    public Mono<Void> unblockAddress(InetSocketAddress address) {
        return Mono.fromRunnable(() -> {
            if (blockedAddresses.remove(address) != null) {
                statistics.decrementBlockedAddresses();
            }
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }

    @Override
    public Mono<Boolean> isBlocked(InetSocketAddress address) {
        return Mono.fromCallable(() -> {
            Long unblockTime = blockedAddresses.get(address);
            if (unblockTime == null) {
                return false;
            }
            
            if (System.currentTimeMillis() > unblockTime) {
                blockedAddresses.remove(address);
                statistics.decrementBlockedAddresses();
                return false;
            }
            
            return true;
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> start() {
        return Mono.fromCallable(() -> {
            if (isRunning.compareAndSet(false, true)) {
                server = UdpServer.create()
                    .host(bindAddress.getHostString())
                    .port(bindAddress.getPort())
                    .handle((inbound, outbound) -> {
                        inbound.receive()
                            .cast(DatagramPacket.class)
                            .subscribe(this::handleIncomingPacket);
                        return Mono.never();
                    })
                    .bindNow();
                
                // Start cleanup task for blocked addresses
                Flux.interval(Duration.ofSeconds(30))
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe(tick -> cleanupBlockedAddresses());
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
                if (server != null) {
                    server.dispose();
                }
                if (client != null) {
                    client.dispose();
                }
                inboundSink.tryEmitComplete();
            }
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return bindAddress;
    }

    @Override
    public Mono<NetworkStatistics> getStatistics() {
        return Mono.just(statistics);
    }

    private void handleIncomingPacket(DatagramPacket packet) {
        try {
            ByteBuf content = packet.content();
            InetSocketAddress sender = packet.sender();
            
            // Check if sender is blocked
            if (isBlocked(sender).block(Duration.ofMillis(100))) {
                statistics.incrementPacketsDropped();
                return;
            }
            
            // Create network packet
            NetworkPacket networkPacket = new NetworkPacket(
                content.retain(), 
                determinePacketType(content)
            );
            
            // Create packet event
            PacketEvent event = new PacketEvent(
                networkPacket, 
                sender, 
                sequenceNumber.incrementAndGet()
            );
            
            // Emit to reactive stream
            inboundSink.tryEmitNext(event);
            
            // Update statistics
            statistics.incrementPacketsReceived();
            statistics.addBytesReceived(content.readableBytes());
            
        } catch (Exception e) {
            statistics.incrementPacketsDropped();
            // Log error but don't crash
            System.err.println("Error handling incoming packet: " + e.getMessage());
        }
    }

    private NetworkPacket.PacketType determinePacketType(ByteBuf buffer) {
        if (buffer.readableBytes() < 1) {
            return NetworkPacket.PacketType.UNKNOWN;
        }
        
        byte firstByte = buffer.getByte(0);
        
        // RakNet packet type determination based on first byte
        switch (firstByte) {
            case 0x01: return NetworkPacket.PacketType.RAKNET_PING;
            case 0x03: return NetworkPacket.PacketType.RAKNET_PONG;
            case 0x05: return NetworkPacket.PacketType.RAKNET_OPEN_CONNECTION_REQUEST_1;
            case 0x06: return NetworkPacket.PacketType.RAKNET_OPEN_CONNECTION_REPLY_1;
            case 0x07: return NetworkPacket.PacketType.RAKNET_OPEN_CONNECTION_REQUEST_2;
            case 0x08: return NetworkPacket.PacketType.RAKNET_OPEN_CONNECTION_REPLY_2;
            case 0x09: return NetworkPacket.PacketType.RAKNET_CONNECTION_REQUEST;
            case 0x10: return NetworkPacket.PacketType.RAKNET_CONNECTION_REQUEST_ACCEPTED;
            case 0x13: return NetworkPacket.PacketType.RAKNET_NEW_INCOMING_CONNECTION;
            case 0x15: return NetworkPacket.PacketType.RAKNET_DISCONNECT_NOTIFICATION;
            case 0x19: return NetworkPacket.PacketType.RAKNET_INCOMPATIBLE_PROTOCOL_VERSION;
            case 0x1c: return NetworkPacket.PacketType.RAKNET_UNCONNECTED_PING;
            case 0x1d: return NetworkPacket.PacketType.RAKNET_UNCONNECTED_PONG;
            case (byte) 0xc0: return NetworkPacket.PacketType.RAKNET_ACK;
            case (byte) 0xa0: return NetworkPacket.PacketType.RAKNET_NACK;
            default:
                if ((firstByte & 0x80) != 0) {
                    return NetworkPacket.PacketType.RAKNET_DATA_PACKET;
                }
                return NetworkPacket.PacketType.UNKNOWN;
        }
    }

    private void cleanupBlockedAddresses() {
        long currentTime = System.currentTimeMillis();
        blockedAddresses.entrySet().removeIf(entry -> {
            if (currentTime > entry.getValue()) {
                statistics.decrementBlockedAddresses();
                return true;
            }
            return false;
        });
    }
}