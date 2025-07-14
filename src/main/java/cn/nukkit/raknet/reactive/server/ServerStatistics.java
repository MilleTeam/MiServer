package cn.nukkit.raknet.reactive.server;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Server-level statistics and metrics tracking.
 */
public class ServerStatistics {
    
    private final AtomicLong totalConnections = new AtomicLong(0);
    private final AtomicLong currentConnections = new AtomicLong(0);
    private final AtomicLong peakConnections = new AtomicLong(0);
    private final AtomicLong totalDisconnections = new AtomicLong(0);
    private final AtomicLong timeoutDisconnections = new AtomicLong(0);
    private final AtomicLong rejectedConnections = new AtomicLong(0);
    private final AtomicLong totalPacketsProcessed = new AtomicLong(0);
    private final AtomicLong totalBytesProcessed = new AtomicLong(0);
    private final AtomicLong totalErrors = new AtomicLong(0);
    private final AtomicLong blockedAddresses = new AtomicLong(0);
    private final Instant serverStartTime;
    private volatile long averageSessionDuration = 0;
    private volatile int maxPlayers = 0;
    private volatile String serverName = "";
    
    public ServerStatistics() {
        this.serverStartTime = Instant.now();
    }
    
    public void incrementTotalConnections() {
        totalConnections.incrementAndGet();
        long current = currentConnections.incrementAndGet();
        updatePeakConnections(current);
    }
    
    public void decrementCurrentConnections() {
        currentConnections.decrementAndGet();
    }
    
    public void incrementTotalDisconnections() {
        totalDisconnections.incrementAndGet();
    }
    
    public void incrementTimeoutDisconnections() {
        timeoutDisconnections.incrementAndGet();
    }
    
    public void incrementRejectedConnections() {
        rejectedConnections.incrementAndGet();
    }
    
    public void addPacketsProcessed(long count) {
        totalPacketsProcessed.addAndGet(count);
    }
    
    public void addBytesProcessed(long bytes) {
        totalBytesProcessed.addAndGet(bytes);
    }
    
    public void incrementTotalErrors() {
        totalErrors.incrementAndGet();
    }
    
    public void incrementBlockedAddresses() {
        blockedAddresses.incrementAndGet();
    }
    
    public void decrementBlockedAddresses() {
        blockedAddresses.decrementAndGet();
    }
    
    public void setAverageSessionDuration(long duration) {
        this.averageSessionDuration = duration;
    }
    
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    
    private void updatePeakConnections(long current) {
        long peak = peakConnections.get();
        while (current > peak) {
            if (peakConnections.compareAndSet(peak, current)) {
                break;
            }
            peak = peakConnections.get();
        }
    }
    
    public long getTotalConnections() {
        return totalConnections.get();
    }
    
    public long getCurrentConnections() {
        return currentConnections.get();
    }
    
    public long getPeakConnections() {
        return peakConnections.get();
    }
    
    public long getTotalDisconnections() {
        return totalDisconnections.get();
    }
    
    public long getTimeoutDisconnections() {
        return timeoutDisconnections.get();
    }
    
    public long getRejectedConnections() {
        return rejectedConnections.get();
    }
    
    public long getTotalPacketsProcessed() {
        return totalPacketsProcessed.get();
    }
    
    public long getTotalBytesProcessed() {
        return totalBytesProcessed.get();
    }
    
    public long getTotalErrors() {
        return totalErrors.get();
    }
    
    public long getBlockedAddresses() {
        return blockedAddresses.get();
    }
    
    public Instant getServerStartTime() {
        return serverStartTime;
    }
    
    public long getAverageSessionDuration() {
        return averageSessionDuration;
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public String getServerName() {
        return serverName;
    }
    
    public Duration getUptime() {
        return Duration.between(serverStartTime, Instant.now());
    }
    
    public double getConnectionRate() {
        Duration uptime = getUptime();
        if (uptime.isZero()) {
            return 0.0;
        }
        return (double) totalConnections.get() / uptime.toHours();
    }
    
    public double getDisconnectionRate() {
        Duration uptime = getUptime();
        if (uptime.isZero()) {
            return 0.0;
        }
        return (double) totalDisconnections.get() / uptime.toHours();
    }
    
    public double getPacketsPerSecond() {
        Duration uptime = getUptime();
        if (uptime.isZero()) {
            return 0.0;
        }
        return (double) totalPacketsProcessed.get() / uptime.toSeconds();
    }
    
    public double getBytesPerSecond() {
        Duration uptime = getUptime();
        if (uptime.isZero()) {
            return 0.0;
        }
        return (double) totalBytesProcessed.get() / uptime.toSeconds();
    }
    
    public double getErrorRate() {
        long totalProcessed = totalPacketsProcessed.get();
        if (totalProcessed == 0) {
            return 0.0;
        }
        return (double) totalErrors.get() / totalProcessed;
    }
    
    public double getRejectionRate() {
        long totalAttempts = totalConnections.get() + rejectedConnections.get();
        if (totalAttempts == 0) {
            return 0.0;
        }
        return (double) rejectedConnections.get() / totalAttempts;
    }
    
    public double getTimeoutRate() {
        long totalDisconnects = totalDisconnections.get();
        if (totalDisconnects == 0) {
            return 0.0;
        }
        return (double) timeoutDisconnections.get() / totalDisconnects;
    }
    
    public double getServerUtilization() {
        if (maxPlayers == 0) {
            return 0.0;
        }
        return (double) currentConnections.get() / maxPlayers;
    }
    
    @Override
    public String toString() {
        return String.format(
            "ServerStatistics{serverName='%s', uptime=%s, totalConnections=%d, currentConnections=%d, " +
            "peakConnections=%d, totalDisconnections=%d, timeoutDisconnections=%d, rejectedConnections=%d, " +
            "totalPacketsProcessed=%d, totalBytesProcessed=%d, totalErrors=%d, blockedAddresses=%d, " +
            "maxPlayers=%d, averageSessionDuration=%d, connectionRate=%.2f, disconnectionRate=%.2f, " +
            "packetsPerSecond=%.2f, bytesPerSecond=%.2f, errorRate=%.4f, rejectionRate=%.4f, " +
            "timeoutRate=%.4f, serverUtilization=%.2f}",
            serverName, getUptime(), totalConnections.get(), currentConnections.get(),
            peakConnections.get(), totalDisconnections.get(), timeoutDisconnections.get(), 
            rejectedConnections.get(), totalPacketsProcessed.get(), totalBytesProcessed.get(),
            totalErrors.get(), blockedAddresses.get(), maxPlayers, averageSessionDuration,
            getConnectionRate(), getDisconnectionRate(), getPacketsPerSecond(), getBytesPerSecond(),
            getErrorRate(), getRejectionRate(), getTimeoutRate(), getServerUtilization()
        );
    }
}