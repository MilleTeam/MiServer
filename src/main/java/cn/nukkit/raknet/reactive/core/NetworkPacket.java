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
    
    public NetworkPacket(ByteBuf data, PacketType type) {
        this(data, type, 0, false);
    }
    
    public NetworkPacket(ByteBuf data, PacketType type, int priority, boolean reliable) {
        this.data = data.retain();
        this.type = type;
        this.priority = priority;
        this.reliable = reliable;
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
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void putMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    public <T> T getMetadata(String key, Class<T> type) {
        Object value = metadata.get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }
    
    public int getDataLength() {
        return data.readableBytes();
    }
    
    public byte[] getDataArray() {
        byte[] array = new byte[data.readableBytes()];
        data.slice().readBytes(array);
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
        return String.format("NetworkPacket{type=%s, priority=%d, reliable=%s, dataLength=%d}", 
                           type, priority, reliable, getDataLength());
    }
    
    public enum PacketType {
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