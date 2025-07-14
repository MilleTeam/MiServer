package cn.nukkit.raknet.reactive.session;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Session-level statistics and metrics tracking.
 */
public class SessionStatistics {
    
    private final AtomicLong packetsReceived = new AtomicLong(0);
    private final AtomicLong packetsSent = new AtomicLong(0);
    private final AtomicLong bytesReceived = new AtomicLong(0);
    private final AtomicLong bytesSent = new AtomicLong(0);
    private final AtomicLong packetsDropped = new AtomicLong(0);
    private final AtomicLong acksReceived = new AtomicLong(0);
    private final AtomicLong acksSent = new AtomicLong(0);
    private final AtomicLong nacksReceived = new AtomicLong(0);
    private final AtomicLong nacksSent = new AtomicLong(0);
    private final AtomicLong reliablePackets = new AtomicLong(0);
    private final AtomicLong unreliablePackets = new AtomicLong(0);
    private final Instant sessionStart;
    private volatile Instant lastActivity;
    private volatile long roundTripTime = 0;
    private volatile int windowSize = 0;
    
    public SessionStatistics() {
        this.sessionStart = Instant.now();
        this.lastActivity = sessionStart;
    }
    
    public void incrementPacketsReceived() {
        packetsReceived.incrementAndGet();
        updateLastActivity();
    }
    
    public void incrementPacketsSent() {
        packetsSent.incrementAndGet();
        updateLastActivity();
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
    
    public void incrementAcksReceived() {
        acksReceived.incrementAndGet();
    }
    
    public void incrementAcksSent() {
        acksSent.incrementAndGet();
    }
    
    public void incrementNacksReceived() {
        nacksReceived.incrementAndGet();
    }
    
    public void incrementNacksSent() {
        nacksSent.incrementAndGet();
    }
    
    public void incrementReliablePackets() {
        reliablePackets.incrementAndGet();
    }
    
    public void incrementUnreliablePackets() {
        unreliablePackets.incrementAndGet();
    }
    
    public void updateLastActivity() {
        lastActivity = Instant.now();
    }
    
    public void setRoundTripTime(long rtt) {
        this.roundTripTime = rtt;
    }
    
    public void setWindowSize(int size) {
        this.windowSize = size;
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
    
    public long getAcksReceived() {
        return acksReceived.get();
    }
    
    public long getAcksSent() {
        return acksSent.get();
    }
    
    public long getNacksReceived() {
        return nacksReceived.get();
    }
    
    public long getNacksSent() {
        return nacksSent.get();
    }
    
    public long getReliablePackets() {
        return reliablePackets.get();
    }
    
    public long getUnreliablePackets() {
        return unreliablePackets.get();
    }
    
    public Instant getSessionStart() {
        return sessionStart;
    }
    
    public Instant getLastActivity() {
        return lastActivity;
    }
    
    public long getRoundTripTime() {
        return roundTripTime;
    }
    
    public int getWindowSize() {
        return windowSize;
    }
    
    public Duration getSessionDuration() {
        return Duration.between(sessionStart, Instant.now());
    }
    
    public Duration getIdleTime() {
        return Duration.between(lastActivity, Instant.now());
    }
    
    public double getPacketsPerSecond() {
        Duration duration = getSessionDuration();
        if (duration.isZero()) {
            return 0.0;
        }
        return (double) (packetsReceived.get() + packetsSent.get()) / duration.toSeconds();
    }
    
    public double getBytesPerSecond() {
        Duration duration = getSessionDuration();
        if (duration.isZero()) {
            return 0.0;
        }
        return (double) (bytesReceived.get() + bytesSent.get()) / duration.toSeconds();
    }
    
    public double getPacketLossRate() {
        long totalPackets = packetsReceived.get() + packetsDropped.get();
        if (totalPackets == 0) {
            return 0.0;
        }
        return (double) packetsDropped.get() / totalPackets;
    }
    
    public double getReliabilityRatio() {
        long totalPackets = reliablePackets.get() + unreliablePackets.get();
        if (totalPackets == 0) {
            return 0.0;
        }
        return (double) reliablePackets.get() / totalPackets;
    }
    
    @Override
    public String toString() {
        return String.format(
            "SessionStatistics{packetsReceived=%d, packetsSent=%d, bytesReceived=%d, bytesSent=%d, " +
            "packetsDropped=%d, acksReceived=%d, acksSent=%d, nacksReceived=%d, nacksSent=%d, " +
            "reliablePackets=%d, unreliablePackets=%d, sessionDuration=%s, idleTime=%s, " +
            "roundTripTime=%d, windowSize=%d, packetsPerSecond=%.2f, bytesPerSecond=%.2f, " +
            "packetLossRate=%.4f, reliabilityRatio=%.4f}",
            packetsReceived.get(), packetsSent.get(), bytesReceived.get(), bytesSent.get(),
            packetsDropped.get(), acksReceived.get(), acksSent.get(), nacksReceived.get(), nacksSent.get(),
            reliablePackets.get(), unreliablePackets.get(), getSessionDuration(), getIdleTime(),
            roundTripTime, windowSize, getPacketsPerSecond(), getBytesPerSecond(),
            getPacketLossRate(), getReliabilityRatio()
        );
    }
}