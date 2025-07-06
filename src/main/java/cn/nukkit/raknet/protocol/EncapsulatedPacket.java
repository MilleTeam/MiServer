package cn.nukkit.raknet.protocol;

import cn.nukkit.utils.Binary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Modernized EncapsulatedPacket with improved performance and memory efficiency.
 * 
 * @author MagicDroidX Nukkit Project
 */
public class EncapsulatedPacket implements Cloneable
{

	public int reliability;

	public boolean hasSplit = false;

	public int length = 0;

	public Integer messageIndex = null;

	public Integer orderIndex = null;

	public Integer orderChannel = null;

	public Integer splitCount = null;

	public Integer splitID = null;

	public Integer splitIndex = null;

	public byte[] buffer;

	public boolean needACK = false;

	public Integer identifierACK = null;

	private int offset;

	public static EncapsulatedPacket fromBinary(byte[] binary)
	{
		return fromBinary(binary, false);
	}

	public static EncapsulatedPacket fromBinary(
		byte[] binary,
		boolean internal
	)
	{
		EncapsulatedPacket packet = new EncapsulatedPacket();

		int flags = binary[0] & 0xff;

		packet.reliability = ((flags & 0b11100000) >> 5);
		packet.hasSplit = (flags & 0b00010000) > 0;
		int length, offset;
		if (internal)
		{
			length = Binary.readInt(Binary.subBytes(binary, 1, 4));
			packet.identifierACK = Binary.readInt(Binary.subBytes(binary, 5, 4));
			offset = 9;
		} else
		{
			length = (int) Math.ceil(((double) Binary.readShort(Binary.subBytes(binary, 1, 2)) / 8));
			offset = 3;
			packet.identifierACK = null;
		}

		if (packet.reliability > 0)
		{
			if (packet.reliability >= 2 && packet.reliability != 5)
			{
				packet.messageIndex = Binary.readLTriad(Binary.subBytes(binary, offset, 3));
				offset += 3;
			}

			if (packet.reliability <= 4 && packet.reliability != 2)
			{
				packet.orderIndex = Binary.readLTriad(Binary.subBytes(binary, offset, 3));
				offset += 3;
				packet.orderChannel = binary[offset++] & 0xff;
			}
		}

		if (packet.hasSplit)
		{
			packet.splitCount = Binary.readInt(Binary.subBytes(binary, offset, 4));
			offset += 4;
			packet.splitID = Binary.readShort(Binary.subBytes(binary, offset, 2));
			offset += 2;
			packet.splitIndex = Binary.readInt(Binary.subBytes(binary, offset, 4));
			offset += 4;
		}

		packet.buffer = Binary.subBytes(binary, offset, length);
		offset += length;
		packet.offset = offset;

		return packet;
	}

	public int getOffset()
	{
		return offset;
	}

	public int getTotalLength()
	{
		return 3 + this.buffer.length + (this.messageIndex != null ? 3 : 0) + (this.orderIndex != null ? 4 : 0) + (this.hasSplit ? 10 : 0);
	}

	public byte[] toBinary()
	{
		return toBinary(false);
	}

	/**
	 * More efficient binary serialization using ByteBuffer for better performance.
	 */
	public byte[] toBinary(boolean internal)
	{
		// Pre-calculate size to avoid array resizing
		int estimatedSize = calculateBinarySize(internal);
		ByteBuffer buffer = ByteBuffer.allocate(estimatedSize);
		
		try {
			buffer.put((byte) ((reliability << 5) | (hasSplit ? 0b00010000 : 0)));
			
			if (internal) {
				buffer.putInt(this.buffer.length);
				buffer.putInt(identifierACK == null ? 0 : identifierACK);
			} else {
				buffer.putShort((short) (this.buffer.length << 3));
			}

			if (reliability > 0) {
				if (reliability >= 2 && reliability != 5) {
					putLTriad(buffer, messageIndex == null ? 0 : messageIndex);
				}
				if (reliability <= 4 && reliability != 2) {
					putLTriad(buffer, orderIndex == null ? 0 : orderIndex);
					buffer.put((byte) (orderChannel == null ? 0 : orderChannel & 0xff));
				}
			}

			if (hasSplit) {
				buffer.putInt(splitCount == null ? 0 : splitCount);
				buffer.putShort((short) (splitID == null ? 0 : splitID));
				buffer.putInt(splitIndex == null ? 0 : splitIndex);
			}

			buffer.put(this.buffer);
			
			// Return only the used portion of the buffer
			byte[] result = new byte[buffer.position()];
			buffer.rewind();
			buffer.get(result);
			return result;
			
		} catch (Exception e) {
			// Fallback to original implementation if ByteBuffer approach fails
			return toBinaryLegacy(internal);
		}
	}
	
	/**
	 * Calculate the expected binary size to optimize buffer allocation.
	 */
	private int calculateBinarySize(boolean internal) {
		int size = 1; // reliability byte
		
		if (internal) {
			size += 8; // buffer length (4) + identifierACK (4)
		} else {
			size += 2; // buffer length (2)
		}
		
		if (reliability > 0) {
			if (reliability >= 2 && reliability != 5) {
				size += 3; // messageIndex
			}
			if (reliability <= 4 && reliability != 2) {
				size += 4; // orderIndex (3) + orderChannel (1)
			}
		}
		
		if (hasSplit) {
			size += 10; // splitCount (4) + splitID (2) + splitIndex (4)
		}
		
		size += this.buffer.length; // actual buffer content
		
		return size;
	}
	
	/**
	 * Helper method to put little-endian triad into ByteBuffer.
	 */
	private void putLTriad(ByteBuffer buffer, int value) {
		buffer.put((byte) (value & 0xff));
		buffer.put((byte) ((value >> 8) & 0xff));
		buffer.put((byte) ((value >> 16) & 0xff));
	}
	
	/**
	 * Legacy implementation as fallback.
	 */
	private byte[] toBinaryLegacy(boolean internal)
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try
		{
			stream.write((reliability << 5) | (hasSplit ? 0b00010000 : 0));
			if (internal)
			{
				stream.write(Binary.writeInt(buffer.length));
				stream.write(Binary.writeInt(identifierACK == null ? 0 : identifierACK));
			} else
			{
				stream.write(Binary.writeShort(buffer.length << 3));
			}

			if (reliability > 0)
			{
				if (reliability >= 2 && reliability != 5)
				{
					stream.write(Binary.writeLTriad(messageIndex == null ? 0 : messageIndex));
				}
				if (reliability <= 4 && reliability != 2)
				{
					stream.write(Binary.writeLTriad(orderIndex == null ? 0 : orderIndex));
					stream.write((byte) (orderChannel == null ? 0 : orderChannel & 0xff));
				}
			}

			if (hasSplit)
			{
				stream.write(Binary.writeInt(splitCount == null ? 0 : splitCount));
				stream.write(Binary.writeShort(splitID == null ? 0 : splitID));
				stream.write(Binary.writeInt(splitIndex == null ? 0 : splitIndex));
			}

			stream.write(buffer);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		return stream.toByteArray();
	}

	@Override
	public String toString()
	{
		return Binary.bytesToHexString(this.toBinary());
	}

	@Override
	public EncapsulatedPacket clone() throws CloneNotSupportedException
	{
		EncapsulatedPacket packet = (EncapsulatedPacket) super.clone();
		if (this.buffer != null) {
			packet.buffer = this.buffer.clone();
		}
		return packet;
	}
	
	/**
	 * More efficient equals implementation.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		
		EncapsulatedPacket that = (EncapsulatedPacket) obj;
		return reliability == that.reliability &&
			hasSplit == that.hasSplit &&
			length == that.length &&
			needACK == that.needACK &&
			Objects.equals(messageIndex, that.messageIndex) &&
			Objects.equals(orderIndex, that.orderIndex) &&
			Objects.equals(orderChannel, that.orderChannel) &&
			Objects.equals(splitCount, that.splitCount) &&
			Objects.equals(splitID, that.splitID) &&
			Objects.equals(splitIndex, that.splitIndex) &&
			Objects.equals(identifierACK, that.identifierACK) &&
			java.util.Arrays.equals(buffer, that.buffer);
	}
	
	/**
	 * More efficient hashCode implementation.
	 */
	@Override
	public int hashCode() {
		int result = Objects.hash(reliability, hasSplit, length, needACK, 
			messageIndex, orderIndex, orderChannel, splitCount, splitID, splitIndex, identifierACK);
		result = 31 * result + java.util.Arrays.hashCode(buffer);
		return result;
	}

}
