package cn.nukkit.level.format.anvil;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseRegionLoader;
import cn.nukkit.utils.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * author: MagicDroidX Nukkit Project
 */
public class RegionLoader extends BaseRegionLoader
{

	public RegionLoader(
		LevelProvider level,
		int regionX,
		int regionZ
	) throws IOException
	{
		super(level, regionX, regionZ, "mca");
	}

	protected static int getChunkOffset(
		int x,
		int z
	)
	{
		return x | (z << 5);
	}

	@Override
	protected boolean isChunkGenerated(int index)
	{
		Integer[] array = this.locationTable.get(index);
		return !(array[0] == 0 || array[1] == 0);
	}

	@Override
	public Chunk readChunk(
		int x,
		int z
	) throws IOException
	{
		int index = getChunkOffset(x, z);
		if (index < 0 || index >= 4096)
		{
			return null;
		}

		this.lastUsed = System.currentTimeMillis();

		if (!this.isChunkGenerated(index))
		{
			return null;
		}

		Integer[] table = this.locationTable.get(index);
		this.randomAccessFile.seek(table[0] << 12);
		int length = this.randomAccessFile.readInt();
		byte compression = this.randomAccessFile.readByte();
		if (length <= 0 || length >= MAX_SECTOR_LENGTH)
		{
			if (length >= MAX_SECTOR_LENGTH)
			{
				table[0] = ++this.lastSector;
				table[1] = 1;
				this.locationTable.put(index, table);
				MainLogger.getLogger().error("Corrupted chunk header detected");
			}
			return null;
		}

		if (length > (table[1] << 12))
		{
			MainLogger.getLogger().error("Corrupted bigger chunk detected");
			table[1] = length >> 12;
			this.locationTable.put(index, table);
			this.writeLocationIndex(index);
		} else if (compression != COMPRESSION_ZLIB && compression != COMPRESSION_GZIP)
		{
			MainLogger.getLogger().error("Invalid compression type");
			return null;
		}

		byte[] data = new byte[length - 1];
		this.randomAccessFile.read(data);
		Chunk chunk = this.unserializeChunk(data);
		if (chunk != null)
		{
			return chunk;
		} else
		{
			MainLogger.getLogger().error("Corrupted chunk detected");
			return null;
		}
	}

	@Override
	protected Chunk unserializeChunk(byte[] data)
	{
		return Chunk.fromBinary(data, this.levelProvider);
	}

	@Override
	public boolean chunkExists(
		int x,
		int z
	)
	{
		return this.isChunkGenerated(getChunkOffset(x, z));
	}

	@Override
	protected void saveChunk(
		int x,
		int z,
		byte[] chunkData
	) throws IOException
	{
		int length = chunkData.length + 1;
		if (length + 4 > MAX_SECTOR_LENGTH)
		{
			throw new ChunkException("Chunk is too big! " + (length + 4) + " > " + MAX_SECTOR_LENGTH);
		}
		int sectors = (int) Math.ceil((length + 4) / 4096d);
		int index = getChunkOffset(x, z);
		boolean indexChanged = false;
		Integer[] table = this.locationTable.get(index);

		if (table[1] < sectors)
		{
			table[0] = this.lastSector + 1;
			this.locationTable.put(index, table);
			this.lastSector += sectors;
			indexChanged = true;
		} else if (table[1] != sectors)
		{
			indexChanged = true;
		}

		table[1] = sectors;
		table[2] = (int) (System.currentTimeMillis() / 1000d);

		this.locationTable.put(index, table);
		this.randomAccessFile.seek(table[0] << 12);

		BinaryStream stream = new BinaryStream();
		stream.put(Binary.writeInt(length));
		stream.putByte(COMPRESSION_ZLIB);
		stream.put(chunkData);
		byte[] data = stream.getBuffer();
		if (data.length < sectors << 12)
		{
			byte[] newData = new byte[sectors << 12];
			System.arraycopy(data, 0, newData, 0, data.length);
			data = newData;
		}

		this.randomAccessFile.write(data);

		if (indexChanged)
		{
			this.writeLocationIndex(index);
		}
	}

	@Override
	public void removeChunk(
		int x,
		int z
	)
	{
		int index = getChunkOffset(x, z);
		Integer[] table = this.locationTable.get(0);
		table[0] = 0;
		table[1] = 0;
		this.locationTable.put(index, table);
	}

	@Override
	public void writeChunk(FullChunk chunk) throws Exception
	{
		this.lastUsed = System.currentTimeMillis();
		byte[] chunkData = chunk.toBinary();
		this.saveChunk(chunk.getX() & 0x1f, chunk.getZ() & 0x1f, chunkData);
	}

	@Override
	public void close() throws IOException
	{
		this.writeLocationTable();
		this.randomAccessFile.close();
		this.levelProvider = null;
	}

	@Override
	public int doSlowCleanUp() throws Exception
	{
		for (int i = 0 ; i < 1024 ; i++)
		{
			Integer[] table = this.locationTable.get(i);
			if (table[0] == 0 || table[1] == 0)
			{
				continue;
			}
			this.randomAccessFile.seek(table[0] << 12);
			byte[] chunk = new byte[table[1] << 12];
			this.randomAccessFile.read(chunk);
			int length = Binary.readInt(Arrays.copyOfRange(chunk, 0, 3));
			if (length <= 1)
			{
				this.locationTable.put(i, (table = new Integer[] { 0, 0, 0 }));
			}
			try
			{
				chunk = Zlib.inflate(Arrays.copyOf(chunk, 5));
			}
			catch (Exception e)
			{
				this.locationTable.put(i, new Integer[] { 0, 0, 0 });
				continue;
			}
			chunk = Zlib.deflate(chunk, 9);
			ByteBuffer buffer = ByteBuffer.allocate(4 + 1 + chunk.length);
			buffer.put(Binary.writeInt(chunk.length + 1));
			buffer.put(COMPRESSION_ZLIB);
			buffer.put(chunk);
			chunk = buffer.array();
			int sectors = (int) Math.ceil(chunk.length / 4096d);
			if (sectors > table[1])
			{
				table[0] = this.lastSector + 1;
				this.lastSector += sectors;
				this.locationTable.put(i, table);
			}
			this.randomAccessFile.seek(table[0] << 12);
			byte[] bytes = new byte[sectors << 12];
			ByteBuffer buffer1 = ByteBuffer.wrap(bytes);
			buffer1.put(chunk);
			this.randomAccessFile.write(buffer1.array());
		}
		this.writeLocationTable();
		int n = this.cleanGarbage();
		this.writeLocationTable();
		return n;
	}

	@Override
	protected void loadLocationTable() throws IOException
	{
		this.randomAccessFile.seek(0);
		this.lastSector = 1;
		int[] data = new int[1024 * 2]; //1024 records * 2 times
		for (int i = 0 ; i < 1024 * 2 ; i++)
		{
			data[i] = this.randomAccessFile.readInt();
		}
		for (int i = 0 ; i < 1024 ; ++i)
		{
			int index = data[i];
			this.locationTable.put(i, new Integer[] { index >> 8, index & 0xff, data[1024 + i] });
			int value = this.locationTable.get(i)[0] + this.locationTable.get(i)[1] - 1;
			if (value > this.lastSector)
			{
				this.lastSector = value;
			}
		}
	}

	private void writeLocationTable() throws IOException
	{
		this.randomAccessFile.seek(0);
		for (int i = 0 ; i < 1024 ; ++i)
		{
			Integer[] array = this.locationTable.get(i);
			this.randomAccessFile.writeInt((array[0] << 8) | array[1]);
		}
		for (int i = 0 ; i < 1024 ; ++i)
		{
			Integer[] array = this.locationTable.get(i);
			this.randomAccessFile.writeInt(array[2]);
		}
	}

	private int cleanGarbage() throws IOException
	{
		Map<Integer, Integer> sectors = new TreeMap<>();
		for (Map.Entry entry : this.locationTable.entrySet())
		{
			Integer index = (Integer) entry.getKey();
			Integer[] data = (Integer[]) entry.getValue();
			if (data[0] == 0 || data[1] == 0)
			{
				this.locationTable.put(index, new Integer[] { 0, 0, 0 });
				continue;
			}
			sectors.put(data[0], index);
		}

		if (sectors.size() == (this.lastSector - 2))
		{
			return 0;
		}
		int shift = 0;
		int lastSector = 1;

		this.randomAccessFile.seek(8192);
		int s = 2;
		for (int sector : sectors.keySet())
		{
			s = sector;
			int index = sectors.get(sector);
			if ((sector - lastSector) > 1)
			{
				shift += sector - lastSector - 1;
			}
			if (shift > 0)
			{
				this.randomAccessFile.seek(sector << 12);
				byte[] old = new byte[4096];
				this.randomAccessFile.read(old);
				this.randomAccessFile.seek((sector - shift) << 12);
				this.randomAccessFile.write(old);
			}
			Integer[] v = this.locationTable.get(index);
			v[0] -= shift;
			this.locationTable.put(index, v);
			this.lastSector = sector;
		}
		this.randomAccessFile.setLength((s + 1) << 12);
		return shift;
	}

	@Override
	protected void writeLocationIndex(int index) throws IOException
	{
		Integer[] array = this.locationTable.get(index);
		this.randomAccessFile.seek(index << 2);
		this.randomAccessFile.writeInt((array[0] << 8) | array[1]);
		this.randomAccessFile.seek(4096 + (index << 2));
		this.randomAccessFile.writeInt(array[2]);
	}

	@Override
	protected void createBlank() throws IOException
	{
		this.randomAccessFile.seek(0);
		this.randomAccessFile.setLength(0);
		this.lastSector = 1;
		int time = (int) (System.currentTimeMillis() / 1000d);
		for (int i = 0 ; i < 1024 ; ++i)
		{
			this.locationTable.put(i, new Integer[] { 0, 0, time });
			this.randomAccessFile.writeInt(0);
		}
		for (int i = 0 ; i < 1024 ; ++i)
		{
			this.randomAccessFile.writeInt(time);
		}
	}

	@Override
	public int getX()
	{
		return x;
	}

	@Override
	public int getZ()
	{
		return z;
	}

}
