package cn.nukkit.raknet.server;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Performance metrics collector for RakNet server.
 * Provides insights into network performance and resource usage.
 * 
 * @author MagicDroidX Nukkit Project
 */
public class RakNetMetrics 
{
    private final AtomicLong packetsReceived = new AtomicLong(0);
    private final AtomicLong packetsSent = new AtomicLong(0);
    private final AtomicLong bytesReceived = new AtomicLong(0);
    private final AtomicLong bytesSent = new AtomicLong(0);
    private final AtomicInteger activeSessions = new AtomicInteger(0);
    private final AtomicInteger blockedAddresses = new AtomicInteger(0);
    private final AtomicLong packetsDropped = new AtomicLong(0);
    private final AtomicLong errorsEncountered = new AtomicLong(0);
    
    private volatile long startTime = System.currentTimeMillis();
    
    /**
     * Record a received packet.
     */
    public void recordPacketReceived(int size) {
        packetsReceived.incrementAndGet();
        bytesReceived.addAndGet(size);
    }
    
    /**
     * Record a sent packet.
     */
    public void recordPacketSent(int size) {
        packetsSent.incrementAndGet();
        bytesSent.addAndGet(size);
    }
    
    /**
     * Record a dropped packet.
     */
    public void recordPacketDropped() {
        packetsDropped.incrementAndGet();
    }
    
    /**
     * Record an error.
     */
    public void recordError() {
        errorsEncountered.incrementAndGet();
    }
    
    /**
     * Update active session count.
     */
    public void setActiveSessions(int count) {
        activeSessions.set(count);
    }
    
    /**
     * Update blocked address count.
     */
    public void setBlockedAddresses(int count) {
        blockedAddresses.set(count);
    }
    
    /**
     * Get total packets received.
     */
    public long getPacketsReceived() {
        return packetsReceived.get();
    }
    
    /**
     * Get total packets sent.
     */
    public long getPacketsSent() {
        return packetsSent.get();
    }
    
    /**
     * Get total bytes received.
     */
    public long getBytesReceived() {
        return bytesReceived.get();
    }
    
    /**
     * Get total bytes sent.
     */
    public long getBytesSent() {
        return bytesSent.get();
    }
    
    /**
     * Get current active sessions.
     */
    public int getActiveSessions() {
        return activeSessions.get();
    }
    
    /**
     * Get current blocked addresses.
     */
    public int getBlockedAddresses() {
        return blockedAddresses.get();
    }
    
    /**
     * Get total packets dropped.
     */
    public long getPacketsDropped() {
        return packetsDropped.get();
    }
    
    /**
     * Get total errors encountered.
     */
    public long getErrorsEncountered() {
        return errorsEncountered.get();
    }
    
    /**
     * Get uptime in milliseconds.
     */
    public long getUptimeMillis() {
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * Get packets per second (receive rate).
     */
    public double getReceiveRate() {
        long uptime = getUptimeMillis();
        return uptime > 0 ? (packetsReceived.get() * 1000.0) / uptime : 0.0;
    }
    
    /**
     * Get packets per second (send rate).
     */
    public double getSendRate() {
        long uptime = getUptimeMillis();
        return uptime > 0 ? (packetsSent.get() * 1000.0) / uptime : 0.0;
    }
    
    /**
     * Get throughput in bytes per second (receive).
     */
    public double getReceiveThroughput() {
        long uptime = getUptimeMillis();
        return uptime > 0 ? (bytesReceived.get() * 1000.0) / uptime : 0.0;
    }
    
    /**
     * Get throughput in bytes per second (send).
     */
    public double getSendThroughput() {
        long uptime = getUptimeMillis();
        return uptime > 0 ? (bytesSent.get() * 1000.0) / uptime : 0.0;
    }
    
    /**
     * Reset all metrics.
     */
    public void reset() {
        packetsReceived.set(0);
        packetsSent.set(0);
        bytesReceived.set(0);
        bytesSent.set(0);
        packetsDropped.set(0);
        errorsEncountered.set(0);
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Get formatted metrics summary.
     */
    public String getSummary() {
        long uptime = getUptimeMillis();
        return String.format(
            "RakNet Metrics - Uptime: %ds, Sessions: %d, " +
            "Packets: %d received / %d sent, " +
            "Bytes: %d received / %d sent, " +
            "Rates: %.2f rx/s / %.2f tx/s, " +
            "Throughput: %.2f B/s rx / %.2f B/s tx, " +
            "Dropped: %d, Errors: %d, Blocked: %d",
            uptime / 1000,
            activeSessions.get(),
            packetsReceived.get(), packetsSent.get(),
            bytesReceived.get(), bytesSent.get(),
            getReceiveRate(), getSendRate(),
            getReceiveThroughput(), getSendThroughput(),
            packetsDropped.get(), errorsEncountered.get(), blockedAddresses.get()
        );
    }
}