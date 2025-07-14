package cn.nukkit.raknet.reactive.session;

import cn.nukkit.raknet.reactive.core.NetworkPacket;
import cn.nukkit.raknet.reactive.core.PacketEvent;
import cn.nukkit.raknet.reactive.core.ReactiveNetworkChannel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Concrete implementation of ReactiveSession for RakNet protocol.
 */
public class ReactiveRakNetSession implements ReactiveSession {

    private final String id;
    private final InetSocketAddress address;
    private final long clientId;
    private final ReactiveNetworkChannel networkChannel;
    private final Duration timeout;
    private final AtomicReference<SessionState> state;
    private final AtomicReference<Instant> lastActivity;
    private final Instant creationTime;
    private final SessionStatistics statistics;
    private final Sinks.Many<PacketEvent> inboundSink;
    private final Flux<PacketEvent> inboundFlux;
    private final ConcurrentHashMap<Long, NetworkPacket> reliablePackets;
    private final ConcurrentHashMap<Long, Instant> pendingAcks;
    private final AtomicLong sequenceNumber;
    private final AtomicLong reliableIndex;
    
    private volatile int mtuSize = 1464; // Default MTU size
    private volatile int windowSize = 2048;
    private volatile long lastPingTime = 0;
    private volatile long roundTripTime = 0;

    public ReactiveRakNetSession(String id, InetSocketAddress address, long clientId, 
                               ReactiveNetworkChannel networkChannel, Duration timeout) {
        this.id = id;
        this.address = address;
        this.clientId = clientId;
        this.networkChannel = networkChannel;
        this.timeout = timeout;
        this.state = new AtomicReference<>(SessionState.UNCONNECTED);
        this.lastActivity = new AtomicReference<>(Instant.now());
        this.creationTime = Instant.now();
        this.statistics = new SessionStatistics();
        this.inboundSink = Sinks.many().multicast().onBackpressureBuffer();
        this.inboundFlux = inboundSink.asFlux()
            .subscribeOn(Schedulers.boundedElastic())
            .publishOn(Schedulers.parallel());
        this.reliablePackets = new ConcurrentHashMap<>();
        this.pendingAcks = new ConcurrentHashMap<>();
        this.sequenceNumber = new AtomicLong(0);
        this.reliableIndex = new AtomicLong(0);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public InetSocketAddress getAddress() {
        return address;
    }

    @Override
    public long getClientId() {
        return clientId;
    }

    @Override
    public SessionState getState() {
        return state.get();
    }

    @Override
    public Instant getCreationTime() {
        return creationTime;
    }

    @Override
    public Instant getLastActivity() {
        return lastActivity.get();
    }

    @Override
    public Duration getTimeout() {
        return timeout;
    }

    @Override
    public int getMtuSize() {
        return mtuSize;
    }

    @Override
    public void setMtuSize(int mtuSize) {
        this.mtuSize = mtuSize;
    }

    @Override
    public Flux<PacketEvent> inbound() {
        return inboundFlux;
    }

    @Override
    public Mono<Void> send(NetworkPacket packet) {
        return send(packet, false, 0);
    }

    @Override
    public Mono<Void> send(NetworkPacket packet, boolean reliable, int priority) {
        return Mono.fromCallable(() -> {
            if (state.get() == SessionState.DISCONNECTED) {
                throw new IllegalStateException("Session is disconnected");
            }
            
            NetworkPacket.PacketType type = packet.getType();
            ByteBuf buffer = packet.getData();
            
            if (reliable) {
                long reliableId = reliableIndex.incrementAndGet();
                packet.putMetadata("reliableId", reliableId);
                reliablePackets.put(reliableId, packet.copy());
                statistics.incrementReliablePackets();
            } else {
                statistics.incrementUnreliablePackets();
            }
            
            // Add sequence number
            long seqNum = sequenceNumber.incrementAndGet();
            packet.putMetadata("sequenceNumber", seqNum);
            
            return packet;
        })
        .flatMap(p -> networkChannel.send(p, address))
        .doOnSuccess(v -> {
            statistics.incrementPacketsSent();
            statistics.addBytesSent(packet.getDataLength());
            updateActivity();
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> handlePacket(PacketEvent event) {
        return Mono.fromRunnable(() -> {
            NetworkPacket packet = event.getPacket();
            
            // Update activity
            updateActivity();
            
            // Update statistics
            statistics.incrementPacketsReceived();
            statistics.addBytesReceived(packet.getDataLength());
            
            // Handle different packet types
            switch (packet.getType()) {
                case RAKNET_PING:
                    handlePing(packet);
                    break;
                case RAKNET_PONG:
                    handlePong(packet);
                    break;
                case RAKNET_OPEN_CONNECTION_REQUEST_1:
                    handleOpenConnectionRequest1(packet);
                    break;
                case RAKNET_OPEN_CONNECTION_REQUEST_2:
                    handleOpenConnectionRequest2(packet);
                    break;
                case RAKNET_CONNECTION_REQUEST:
                    handleConnectionRequest(packet);
                    break;
                case RAKNET_NEW_INCOMING_CONNECTION:
                    handleNewIncomingConnection(packet);
                    break;
                case RAKNET_DISCONNECT_NOTIFICATION:
                    handleDisconnectNotification(packet);
                    break;
                case RAKNET_ACK:
                    handleAck(packet);
                    break;
                case RAKNET_NACK:
                    handleNack(packet);
                    break;
                case RAKNET_DATA_PACKET:
                    handleDataPacket(packet);
                    break;
                default:
                    // Forward to application layer
                    inboundSink.tryEmitNext(event);
                    break;
            }
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }

    @Override
    public Mono<Void> sendAck(long sequenceNumber) {
        return Mono.fromCallable(() -> {
            ByteBuf buffer = Unpooled.buffer();
            buffer.writeByte(0xc0); // ACK packet ID
            buffer.writeLong(sequenceNumber);
            
            return new NetworkPacket(buffer, NetworkPacket.PacketType.RAKNET_ACK);
        })
        .flatMap(packet -> networkChannel.send(packet, address))
        .doOnSuccess(v -> {
            statistics.incrementAcksSent();
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> sendNack(long sequenceNumber) {
        return Mono.fromCallable(() -> {
            ByteBuf buffer = Unpooled.buffer();
            buffer.writeByte(0xa0); // NACK packet ID
            buffer.writeLong(sequenceNumber);
            
            return new NetworkPacket(buffer, NetworkPacket.PacketType.RAKNET_NACK);
        })
        .flatMap(packet -> networkChannel.send(packet, address))
        .doOnSuccess(v -> {
            statistics.incrementNacksSent();
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> isActive() {
        return Mono.fromCallable(() -> {
            SessionState currentState = state.get();
            return currentState == SessionState.CONNECTED || 
                   currentState == SessionState.CONNECTING_1 || 
                   currentState == SessionState.CONNECTING_2;
        });
    }

    @Override
    public Mono<Boolean> isTimedOut() {
        return Mono.fromCallable(() -> {
            Instant lastAct = lastActivity.get();
            return Duration.between(lastAct, Instant.now()).compareTo(timeout) > 0;
        });
    }

    @Override
    public Mono<Void> updateActivity() {
        return Mono.fromRunnable(() -> {
            lastActivity.set(Instant.now());
            statistics.updateLastActivity();
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }

    @Override
    public Mono<Void> disconnect() {
        return disconnect("Client disconnected");
    }

    @Override
    public Mono<Void> disconnect(String reason) {
        return Mono.fromCallable(() -> {
            if (state.compareAndSet(SessionState.CONNECTED, SessionState.DISCONNECTING) ||
                state.compareAndSet(SessionState.CONNECTING_1, SessionState.DISCONNECTING) ||
                state.compareAndSet(SessionState.CONNECTING_2, SessionState.DISCONNECTING)) {
                
                // Send disconnect notification
                ByteBuf buffer = Unpooled.buffer();
                buffer.writeByte(0x15); // Disconnect notification
                byte[] reasonBytes = reason.getBytes();
                buffer.writeShort(reasonBytes.length);
                buffer.writeBytes(reasonBytes);
                
                return new NetworkPacket(buffer, NetworkPacket.PacketType.RAKNET_DISCONNECT_NOTIFICATION);
            }
            return null;
        })
        .flatMap(packet -> {
            if (packet != null) {
                return networkChannel.send(packet, address);
            }
            return Mono.empty();
        })
        .doFinally(signal -> {
            state.set(SessionState.DISCONNECTED);
            inboundSink.tryEmitComplete();
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<SessionStatistics> getStatistics() {
        return Mono.fromCallable(() -> {
            statistics.setRoundTripTime(roundTripTime);
            statistics.setWindowSize(windowSize);
            return statistics;
        });
    }

    private void handlePing(NetworkPacket packet) {
        // Send pong response
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeByte(0x03); // Pong packet ID
        buffer.writeLong(System.currentTimeMillis());
        
        NetworkPacket pongPacket = new NetworkPacket(buffer, NetworkPacket.PacketType.RAKNET_PONG);
        send(pongPacket).subscribe();
    }

    private void handlePong(NetworkPacket packet) {
        // Calculate round trip time
        ByteBuf buffer = packet.getData();
        if (buffer.readableBytes() >= 8) {
            long pingTime = buffer.readLong();
            roundTripTime = System.currentTimeMillis() - pingTime;
        }
    }

    private void handleOpenConnectionRequest1(NetworkPacket packet) {
        state.set(SessionState.CONNECTING_1);
        
        // Send open connection reply 1
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeByte(0x06); // Open connection reply 1
        buffer.writeLong(clientId);
        buffer.writeBoolean(false); // No security
        buffer.writeShort(mtuSize);
        
        NetworkPacket reply = new NetworkPacket(buffer, NetworkPacket.PacketType.RAKNET_OPEN_CONNECTION_REPLY_1);
        send(reply).subscribe();
    }

    private void handleOpenConnectionRequest2(NetworkPacket packet) {
        state.set(SessionState.CONNECTING_2);
        
        // Send open connection reply 2
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeByte(0x08); // Open connection reply 2
        buffer.writeLong(clientId);
        buffer.writeBytes(address.getAddress().getAddress());
        buffer.writeShort(address.getPort());
        buffer.writeShort(mtuSize);
        buffer.writeBoolean(false); // No encryption
        
        NetworkPacket reply = new NetworkPacket(buffer, NetworkPacket.PacketType.RAKNET_OPEN_CONNECTION_REPLY_2);
        send(reply).subscribe();
    }

    private void handleConnectionRequest(NetworkPacket packet) {
        // Send connection request accepted
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeByte(0x10); // Connection request accepted
        buffer.writeBytes(address.getAddress().getAddress());
        buffer.writeShort(address.getPort());
        buffer.writeShort(0); // System index
        buffer.writeLong(System.currentTimeMillis());
        buffer.writeLong(System.currentTimeMillis());
        
        NetworkPacket reply = new NetworkPacket(buffer, NetworkPacket.PacketType.RAKNET_CONNECTION_REQUEST_ACCEPTED);
        send(reply).subscribe();
    }

    private void handleNewIncomingConnection(NetworkPacket packet) {
        state.set(SessionState.CONNECTED);
    }

    private void handleDisconnectNotification(NetworkPacket packet) {
        state.set(SessionState.DISCONNECTED);
        inboundSink.tryEmitComplete();
    }

    private void handleAck(NetworkPacket packet) {
        statistics.incrementAcksReceived();
        
        ByteBuf buffer = packet.getData();
        if (buffer.readableBytes() >= 8) {
            long seqNum = buffer.readLong();
            pendingAcks.remove(seqNum);
            
            // Remove from reliable packets if this was a reliable packet
            reliablePackets.remove(seqNum);
        }
    }

    private void handleNack(NetworkPacket packet) {
        statistics.incrementNacksReceived();
        
        ByteBuf buffer = packet.getData();
        if (buffer.readableBytes() >= 8) {
            long seqNum = buffer.readLong();
            
            // Resend reliable packet if it exists
            NetworkPacket reliablePacket = reliablePackets.get(seqNum);
            if (reliablePacket != null) {
                send(reliablePacket.copy(), true, 1).subscribe();
            }
        }
    }

    private void handleDataPacket(NetworkPacket packet) {
        // Extract sequence number and send ACK
        Long seqNum = packet.getMetadata("sequenceNumber", Long.class);
        if (seqNum != null) {
            sendAck(seqNum).subscribe();
        }
        
        // Forward to application layer
        PacketEvent event = new PacketEvent(packet, address, seqNum != null ? seqNum : 0);
        inboundSink.tryEmitNext(event);
    }
}