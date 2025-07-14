package cn.nukkit.raknet.reactive.core;

import cn.nukkit.raknet.reactive.core.NetworkPacket;
import cn.nukkit.raknet.reactive.core.PacketEvent;
import cn.nukkit.raknet.reactive.core.ReactiveNetworkChannel;
import cn.nukkit.raknet.reactive.core.NetworkStatistics;
import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.netty.udp.UdpClient;
import reactor.netty.udp.UdpServer;
import reactor.netty.Connection;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Reactive UDP channel implementation using Reactor Netty
 */
public class ReactiveUDPChannel implements ReactiveNetworkChannel {

    private final InetSocketAddress bindAddress;
    private final NetworkStatistics statistics;
    private final ConcurrentHashMap<InetSocketAddress, Long> blockedAddresses;
    private final AtomicLong sequenceNumber;
    private final AtomicBoolean isRunning;
    private final Sinks.Many<PacketEvent> inboundSink;
    private final Flux<PacketEvent> inboundFlux;
    
    private Connection serverConnection;
    private UdpClient client;

    public ReactiveUDPChannel(String host, int port) {
        this.bindAddress = new InetSocketAddress(host, port);
        this.statistics = new NetworkStatistics();
        this.blockedAddresses = new ConcurrentHashMap<>();
        this.sequenceNumber = new AtomicLong(0);
        this.isRunning = new AtomicBoolean(false);
        this.inboundSink = Sinks.many().multicast().onBackpressureBuffer();
        this.inboundFlux = inboundSink.asFlux();
    }

    @Override
    public Flux<PacketEvent> inbound() {
        return inboundFlux;
    }

    @Override
    public Mono<Void> send(NetworkPacket packet, InetSocketAddress address) {
        return Mono.fromRunnable(() -> {
            if (isBlocked(address).block()) {
                statistics.incrementPacketsDropped();
                return;
            }
            statistics.incrementPacketsSent();
        })
        .then(
            Mono.fromCallable(() -> {
                DatagramPacket datagramPacket = new DatagramPacket(
                    packet.getData().copy(),
                    address
                );
                
                return datagramPacket;
            }))
        .flatMap(datagramPacket -> {
            if (client == null) {
                client = UdpClient.create();
            }
            return client.connect()
                .flatMap(connection -> 
                    connection.outbound()
                        .sendObject(Mono.just(datagramPacket))
                        .then()
                );
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
            .retry(maxRetries)
            .delaySubscription(retryDelay);
    }

    @Override
    public Mono<Void> blockAddress(InetSocketAddress address, Duration duration) {
        return Mono.fromRunnable(() -> {
            long expiryTime = System.currentTimeMillis() + duration.toMillis();
            blockedAddresses.put(address, expiryTime);
        });
    }

    @Override
    public Mono<Void> unblockAddress(InetSocketAddress address) {
        return Mono.fromRunnable(() -> {
            blockedAddresses.remove(address);
        });
    }

    @Override
    public Mono<Boolean> isBlocked(InetSocketAddress address) {
        return Mono.fromCallable(() -> {
            Long expiryTime = blockedAddresses.get(address);
            if (expiryTime == null) {
                return false;
            }
            
            if (System.currentTimeMillis() > expiryTime) {
                blockedAddresses.remove(address);
                return false;
            }
            
            return true;
        });
    }

    @Override
    public Mono<Void> start() {
        return Mono.fromRunnable(() -> {
            if (isRunning.compareAndSet(false, true)) {
                serverConnection = UdpServer.create()
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
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }

    @Override
    public Mono<Void> stop() {
        return Mono.fromRunnable(() -> {
            if (isRunning.compareAndSet(true, false)) {
                if (serverConnection != null) {
                    serverConnection.dispose();
                }
                if (client != null) {
                    // UdpClient doesn't have dispose method, we'll handle lifecycle differently
                    client = null;
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
            statistics.incrementPacketsReceived();
            
            // Check if address is blocked
            if (isBlocked(packet.sender()).block()) {
                statistics.incrementPacketsDropped();
                return;
            }
            
            ByteBuf buffer = packet.content();
            if (buffer.readableBytes() == 0) {
                statistics.incrementPacketsDropped();
                return;
            }
            
            NetworkPacket.PacketType packetType = determinePacketType(buffer);
            
            NetworkPacket networkPacket = new NetworkPacket(
                buffer.copy(),
                packetType,
                sequenceNumber.getAndIncrement(),
                System.currentTimeMillis()
            );
            
            PacketEvent event = new PacketEvent(
                networkPacket,
                packet.sender(),
                sequenceNumber.get()
            );
            
            inboundSink.tryEmitNext(event);
            
        } catch (Exception e) {
            statistics.incrementPacketsDropped();
        }
    }

    private NetworkPacket.PacketType determinePacketType(ByteBuf buffer) {
        if (buffer.readableBytes() == 0) {
            return NetworkPacket.PacketType.UNKNOWN;
        }
        
        byte firstByte = buffer.getByte(buffer.readerIndex());
        
        // RakNet packet type determination logic
        if ((firstByte & 0x80) != 0) {
            return NetworkPacket.PacketType.DATA;
        } else if (firstByte == 0x01) {
            return NetworkPacket.PacketType.PING;
        } else if (firstByte == 0x1C) {
            return NetworkPacket.PacketType.PONG;
        } else {
            return NetworkPacket.PacketType.CONTROL;
        }
    }

    private void cleanupBlockedAddresses() {
        long currentTime = System.currentTimeMillis();
        blockedAddresses.entrySet().removeIf(entry -> entry.getValue() < currentTime);
    }
}