package cn.nukkit.raknet.reactive.core;

import java.net.InetSocketAddress;
import java.time.Instant;

/**
 * Represents an incoming packet event in the reactive stream.
 */
public class PacketEvent {
    
    private final NetworkPacket packet;
    private final InetSocketAddress sender;
    private final Instant timestamp;
    private final long sequenceNumber;
    
    public PacketEvent(NetworkPacket packet, InetSocketAddress sender, long sequenceNumber) {
        this.packet = packet;
        this.sender = sender;
        this.timestamp = Instant.now();
        this.sequenceNumber = sequenceNumber;
    }
    
    public NetworkPacket getPacket() {
        return packet;
    }
    
    public InetSocketAddress getSender() {
        return sender;
    }
    
    public InetSocketAddress getSource() {
        return sender;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public long getSequenceNumber() {
        return sequenceNumber;
    }
    
    @Override
    public String toString() {
        return String.format("PacketEvent{packet=%s, sender=%s, timestamp=%s, sequenceNumber=%d}", 
                           packet, sender, timestamp, sequenceNumber);
    }
}