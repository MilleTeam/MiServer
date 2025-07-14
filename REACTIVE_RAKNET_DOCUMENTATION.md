# Reactive RakNet Implementation Documentation

## Overview

This document describes the complete rewrite of the RakNet networking implementation using **Reactor Netty** for reactive networking and clean architecture principles. The new implementation provides better performance, scalability, and maintainability compared to the original synchronous approach.

## Architecture Overview

The reactive RakNet implementation is built using a layered architecture with clear separation of concerns:

### 1. Core Layer (`cn.nukkit.raknet.reactive.core`)
- **ReactiveNetworkChannel**: Core abstraction for reactive networking operations
- **ReactiveUDPChannel**: Concrete implementation using Reactor Netty
- **NetworkPacket**: Packet data structure with metadata support
- **PacketEvent**: Event representation for incoming packets
- **NetworkStatistics**: Network-level metrics and statistics

### 2. Session Layer (`cn.nukkit.raknet.reactive.session`)
- **ReactiveSession**: Interface for managing individual client connections
- **ReactiveRakNetSession**: Concrete implementation with RakNet protocol support
- **SessionStatistics**: Session-level metrics and statistics

### 3. Server Layer (`cn.nukkit.raknet.reactive.server`)
- **ReactiveSessionManager**: Interface for managing multiple sessions
- **ReactiveRakNetSessionManager**: Concrete implementation with session lifecycle management
- **ServerStatistics**: Server-level metrics and statistics

### 4. Integration Layer (`cn.nukkit.network`)
- **ReactiveRakNetInterface**: Bridge between reactive implementation and existing Nukkit server

## Key Features

### Reactive Programming
- **Non-blocking I/O**: All operations are asynchronous and non-blocking
- **Backpressure Handling**: Automatic handling of flow control
- **Error Resilience**: Robust error handling and recovery
- **Resource Management**: Efficient memory and connection management

### Clean Architecture
- **Separation of Concerns**: Clear boundaries between layers
- **Dependency Inversion**: Depends on abstractions, not concretions
- **Testability**: Easy to unit test individual components
- **Extensibility**: Easy to extend and modify

### Performance
- **Reactive Streams**: Efficient processing of packet streams
- **Connection Pooling**: Reuse of network connections
- **Memory Management**: Proper ByteBuf lifecycle management
- **Statistics Tracking**: Real-time performance monitoring

## Usage

### Basic Setup

```java
// Create network channel
ReactiveUDPChannel networkChannel = new ReactiveUDPChannel("0.0.0.0", 19132);

// Create session manager
ReactiveRakNetSessionManager sessionManager = new ReactiveRakNetSessionManager(networkChannel);

// Configure session manager
sessionManager.setServerName("My Minecraft Server");
sessionManager.setMaxPlayers(100);
sessionManager.setSessionTimeout(Duration.ofMinutes(5));

// Start the server
sessionManager.start().block();
```

### Session Management

```java
// Create a new session
sessionManager.createSession(clientAddress, clientId)
    .subscribe(session -> {
        System.out.println("Session created: " + session.getId());
    });

// Get active sessions
sessionManager.getActiveSessions()
    .subscribe(session -> {
        System.out.println("Active session: " + session.getId());
    });

// Handle session events
sessionManager.sessionEvents()
    .subscribe(event -> {
        switch (event.getType()) {
            case SESSION_CREATED:
                System.out.println("New session: " + event.getSession().getId());
                break;
            case SESSION_DISCONNECTED:
                System.out.println("Session disconnected: " + event.getSession().getId());
                break;
        }
    });
```

### Packet Handling

```java
// Handle incoming packets
sessionManager.inbound()
    .subscribe(packetEvent -> {
        NetworkPacket packet = packetEvent.getPacket();
        InetSocketAddress sender = packetEvent.getSender();
        
        // Process packet based on type
        switch (packet.getType()) {
            case RAKNET_PING:
                handlePing(packet, sender);
                break;
            case RAKNET_DATA_PACKET:
                handleDataPacket(packet, sender);
                break;
        }
    });

// Send packets
NetworkPacket packet = new NetworkPacket(
    Unpooled.wrappedBuffer(data),
    NetworkPacket.PacketType.CUSTOM_PACKET
);

session.send(packet).subscribe();
```

### Statistics and Monitoring

```java
// Get network statistics
sessionManager.getNetworkStatistics()
    .subscribe(stats -> {
        System.out.println("Packets received: " + stats.getPacketsReceived());
        System.out.println("Bytes sent: " + stats.getBytesSent());
        System.out.println("Active connections: " + stats.getActiveConnections());
    });

// Get server statistics
sessionManager.getServerStatistics()
    .subscribe(stats -> {
        System.out.println("Server uptime: " + stats.getUptime());
        System.out.println("Peak connections: " + stats.getPeakConnections());
        System.out.println("Server utilization: " + stats.getServerUtilization());
    });
```

## Configuration

### Session Configuration

```java
// Set session timeout
sessionManager.setSessionTimeout(Duration.ofMinutes(5));

// Set maximum players
sessionManager.setMaxPlayers(200);

// Set server name
sessionManager.setServerName("My Reactive Server");
```

### Network Configuration

```java
// Block an address
sessionManager.blockAddress(address, Duration.ofHours(1))
    .subscribe();

// Unblock an address
sessionManager.unblockAddress(address)
    .subscribe();

// Check if address is blocked
sessionManager.isBlocked(address)
    .subscribe(blocked -> {
        if (blocked) {
            System.out.println("Address is blocked");
        }
    });
```

## Error Handling

The reactive implementation provides robust error handling:

```java
// Handle errors in packet processing
sessionManager.inbound()
    .onErrorResume(error -> {
        logger.error("Error processing packet", error);
        return Flux.empty(); // Continue processing
    })
    .subscribe(packetEvent -> {
        // Process packet
    });

// Handle session creation errors
sessionManager.createSession(address, clientId)
    .onErrorResume(error -> {
        if (error.getMessage().contains("Server is full")) {
            // Handle server full error
            return Mono.empty();
        }
        return Mono.error(error);
    })
    .subscribe();
```

## Migration from Old Implementation

### Key Differences

1. **Asynchronous Operations**: All operations return `Mono<T>` or `Flux<T>`
2. **Reactive Streams**: Use reactive streams for packet processing
3. **Improved Error Handling**: Better error propagation and recovery
4. **Statistics**: Built-in comprehensive statistics tracking
5. **Resource Management**: Automatic resource cleanup

### Migration Steps

1. **Replace RakNetInterface**: Use `ReactiveRakNetInterface` instead of `RakNetInterface`
2. **Update Packet Handling**: Convert to reactive streams
3. **Update Configuration**: Use new configuration methods
4. **Update Error Handling**: Use reactive error handling patterns

### Example Migration

**Old Implementation:**
```java
public void handlePacket(byte[] data, String address, int port) {
    try {
        // Synchronous processing
        DataPacket packet = parsePacket(data);
        processPacket(packet);
    } catch (Exception e) {
        logger.error("Error processing packet", e);
    }
}
```

**New Implementation:**
```java
public void handlePacket(PacketEvent event) {
    Mono.fromCallable(() -> parsePacket(event.getPacket().getDataArray()))
        .flatMap(this::processPacket)
        .onErrorResume(error -> {
            logger.error("Error processing packet", error);
            return Mono.empty();
        })
        .subscribe();
}
```

## Performance Considerations

### Best Practices

1. **Use Reactive Streams**: Leverage reactive streams for efficient processing
2. **Avoid Blocking**: Never block in reactive chains
3. **Resource Management**: Always release ByteBuf resources
4. **Error Handling**: Implement proper error handling and recovery
5. **Monitoring**: Use built-in statistics for performance monitoring

### Performance Tuning

1. **Buffer Sizes**: Configure appropriate buffer sizes for your use case
2. **Thread Pools**: Use appropriate schedulers for different operations
3. **Connection Limits**: Set appropriate connection limits
4. **Timeout Values**: Configure appropriate timeout values

## Testing

### Unit Testing

```java
@Test
public void testSessionCreation() {
    ReactiveNetworkChannel channel = new ReactiveUDPChannel("localhost", 0);
    ReactiveRakNetSessionManager manager = new ReactiveRakNetSessionManager(channel);
    
    StepVerifier.create(manager.createSession(new InetSocketAddress("127.0.0.1", 12345), 123L))
        .expectNextMatches(session -> session.getId() != null)
        .verifyComplete();
}
```

### Integration Testing

```java
@Test
public void testPacketProcessing() {
    // Setup
    ReactiveRakNetSessionManager manager = createSessionManager();
    
    // Test packet processing
    StepVerifier.create(manager.inbound().take(1))
        .then(() -> sendTestPacket())
        .expectNextMatches(event -> event.getPacket().getType() == NetworkPacket.PacketType.RAKNET_PING)
        .verifyComplete();
}
```

## Troubleshooting

### Common Issues

1. **Memory Leaks**: Ensure ByteBuf resources are properly released
2. **Blocking Operations**: Avoid blocking operations in reactive chains
3. **Error Handling**: Implement proper error handling to prevent stream termination
4. **Connection Management**: Monitor connection counts and limits

### Debugging

1. **Enable Debug Logging**: Use debug logging for detailed information
2. **Monitor Statistics**: Use built-in statistics for performance monitoring
3. **Thread Dumps**: Analyze thread dumps for blocking operations
4. **Memory Analysis**: Use memory profilers to detect leaks

## Conclusion

The reactive RakNet implementation provides a modern, scalable, and maintainable solution for Minecraft server networking. By leveraging reactive programming principles and clean architecture, it offers improved performance, better error handling, and easier maintenance compared to the original implementation.

The implementation is designed to be drop-in compatible with the existing Nukkit server while providing significant improvements in scalability and performance. The comprehensive statistics and monitoring capabilities also make it easier to optimize and troubleshoot network performance.