package cn.nukkit.raknet.reactive.core;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Network statistics and metrics tracking for reactive RakNet implementation.
 */
public class NetworkStatistics {
    
    private final AtomicLong packetsReceived = new AtomicLong(0);
    private final AtomicLong packetsSent = new AtomicLong(0);
    private final AtomicLong bytesReceived = new AtomicLong(0);
    private final AtomicLong bytesSent = new AtomicLong(0);
    private final AtomicLong packetsDropped = new AtomicLong(0);
    private final AtomicLong connectionCount = new AtomicLong(0);
    private final AtomicLong activeConnections = new AtomicLong(0);
    private final AtomicLong blockedAddresses = new AtomicLong(0);
    private final Instant startTime;
    
    public NetworkStatistics() {
        this.startTime = Instant.now();
    }
    
    public void incrementPacketsReceived() {
        packetsReceived.incrementAndGet();
    }
    
    public void incrementPacketsSent() {
        packetsSent.incrementAndGet();
    }
    
    public void addBytesReceived(long bytes) {
        bytesReceived.addAndGet(bytes);
    }
    
    public void addBytesSent(long bytes) {
        bytesSent.addAndGet(bytes);
    }
    
    public void incrementPacketsDropped() {
        packetsDropped.incrementAndGet();
    }
    
    public void incrementConnectionCount() {
        connectionCount.incrementAndGet();
        activeConnections.incrementAndGet();
    }
    
    public void decrementActiveConnections() {
        activeConnections.decrementAndGet();
    }
    
    public void incrementBlockedAddresses() {
        blockedAddresses.incrementAndGet();
    }
    
    public void decrementBlockedAddresses() {
        blockedAddresses.decrementAndGet();
    }
    
    public long getPacketsReceived() {
        return packetsReceived.get();
    }
    
    public long getPacketsSent() {
        return packetsSent.get();
    }
    
    public long getBytesReceived() {
        return bytesReceived.get();
    }
    
    public long getBytesSent() {
        return bytesSent.get();
    }
    
    public long getPacketsDropped() {
        return packetsDropped.get();
    }
    
    public long getConnectionCount() {
        return connectionCount.get();
    }
    
    public long getActiveConnections() {
        return activeConnections.get();
    }
    
    public long getBlockedAddresses() {
        return blockedAddresses.get();
    }
    
    public Duration getUptime() {
        return Duration.between(startTime, Instant.now());
    }
    
    public double getPacketsPerSecond() {
        Duration uptime = getUptime();
        if (uptime.isZero()) {
            return 0.0;
        }
        return (double) (packetsReceived.get() + packetsSent.get()) / uptime.toSeconds();
    }
    
    public double getBytesPerSecond() {
        Duration uptime = getUptime();
        if (uptime.isZero()) {
            return 0.0;
        }
        return (double) (bytesReceived.get() + bytesSent.get()) / uptime.toSeconds();
    }
    
    public double getPacketLossRate() {
        long totalPackets = packetsReceived.get() + packetsDropped.get();
        if (totalPackets == 0) {
            return 0.0;
        }
        return (double) packetsDropped.get() / totalPackets;
    }
    
    public double getAverageLatency() {
        // Return a default latency value for now
        // In a real implementation, this would track actual RTT measurements
        return 50.0; // Default 50ms
    }
    
    @Override
    public String toString() {
        return String.format(
            "NetworkStatistics{packetsReceived=%d, packetsSent=%d, bytesReceived=%d, bytesSent=%d, " +
            "packetsDropped=%d, activeConnections=%d, blockedAddresses=%d, uptime=%s, " +
            "packetsPerSecond=%.2f, bytesPerSecond=%.2f, packetLossRate=%.4f}",
            packetsReceived.get(), packetsSent.get(), bytesReceived.get(), bytesSent.get(),
            packetsDropped.get(), activeConnections.get(), blockedAddresses.get(), getUptime(),
            getPacketsPerSecond(), getBytesPerSecond(), getPacketLossRate()
        );
    }
}