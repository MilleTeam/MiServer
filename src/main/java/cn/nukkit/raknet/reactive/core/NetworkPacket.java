package cn.nukkit.raknet.reactive.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a network packet with its data and metadata.
 */
public class NetworkPacket {
    
    private final ByteBuf data;
    private final PacketType type;
    private final Map<String, Object> metadata;
    private final int priority;
    private final boolean reliable;
    private final long sequenceNumber;
    private final long timestamp;
    
    public NetworkPacket(ByteBuf data, PacketType type) {
        this(data, type, 0, false);
    }
    
    public NetworkPacket(ByteBuf data, PacketType type, int priority, boolean reliable) {
        this.data = data.retain();
        this.type = type;
        this.priority = priority;
        this.reliable = reliable;
        this.sequenceNumber = 0;
        this.timestamp = System.currentTimeMillis();
        this.metadata = new HashMap<>();
    }
    
    public NetworkPacket(ByteBuf data, PacketType type, long sequenceNumber, long timestamp) {
        this.data = data.retain();
        this.type = type;
        this.priority = 0;
        this.reliable = false;
        this.sequenceNumber = sequenceNumber;
        this.timestamp = timestamp;
        this.metadata = new HashMap<>();
    }
    
    public ByteBuf getData() {
        return data.slice();
    }
    
    public PacketType getType() {
        return type;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public boolean isReliable() {
        return reliable;
    }
    
    public long getSequenceNumber() {
        return sequenceNumber;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void putMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key, Class<T> type) {
        return (T) metadata.get(key);
    }
    
    public int getDataLength() {
        return data.readableBytes();
    }
    
    public byte[] getDataArray() {
        byte[] array = new byte[data.readableBytes()];
        data.getBytes(data.readerIndex(), array);
        return array;
    }
    
    public void release() {
        data.release();
    }
    
    public NetworkPacket copy() {
        return new NetworkPacket(data.copy(), type, priority, reliable);
    }
    
    @Override
    public String toString() {
        return "NetworkPacket{type=" + type + ", priority=" + priority + ", reliable=" + reliable + ", length=" + getDataLength() + "}";
    }
    
    public enum PacketType {
        PING,
        PONG,
        DATA,
        CONTROL,
        RAKNET_PING,
        RAKNET_PONG,
        RAKNET_OPEN_CONNECTION_REQUEST_1,
        RAKNET_OPEN_CONNECTION_REPLY_1,
        RAKNET_OPEN_CONNECTION_REQUEST_2,
        RAKNET_OPEN_CONNECTION_REPLY_2,
        RAKNET_CONNECTION_REQUEST,
        RAKNET_CONNECTION_REQUEST_ACCEPTED,
        RAKNET_NEW_INCOMING_CONNECTION,
        RAKNET_DISCONNECT_NOTIFICATION,
        RAKNET_INCOMPATIBLE_PROTOCOL_VERSION,
        RAKNET_UNCONNECTED_PING,
        RAKNET_UNCONNECTED_PONG,
        RAKNET_DATA_PACKET,
        RAKNET_ACK,
        RAKNET_NACK,
        CUSTOM_PACKET,
        UNKNOWN
    }
}