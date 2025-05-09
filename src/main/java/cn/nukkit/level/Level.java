package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockentity.BlockEntityShulkerBox;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.level.*;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.BaseLevelProvider;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.level.format.leveldb.LevelDB;
import cn.nukkit.level.format.mcregion.McRegion;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.task.*;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.sound.BlockPlaceSound;
import cn.nukkit.level.sound.Sound;
import cn.nukkit.math.*;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.metadata.BlockMetadataStore;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.protocol.*;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.*;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;

/**
 * author: MagicDroidX Nukkit Project
 */
public class Level implements ChunkManager, Metadatable
{

	public static final int BLOCK_UPDATE_NORMAL = 1;

	public static final int BLOCK_UPDATE_RANDOM = 2;

	public static final int BLOCK_UPDATE_SCHEDULED = 3;

	public static final int BLOCK_UPDATE_WEAK = 4;

	public static final int BLOCK_UPDATE_TOUCH = 5;

	public static final int BLOCK_UPDATE_REDSTONE = 6;

	public static final int BLOCK_UPDATE_TICK = 7;

	public static final int TIME_DAY = 0;

	public static final int TIME_SUNSET = 12000;

	public static final int TIME_NIGHT = 14000;

	public static final int TIME_SUNRISE = 23000;

	public static final int TIME_FULL = 24000;

	public static final int DIMENSION_OVERWORLD = 0;

	public static final int DIMENSION_NETHER = 1;

	// Lower values use less memory
	public static final int MAX_BLOCK_CACHE = 512;

	public static int COMPRESSION_LEVEL = 8;

	private static int levelIdCounter = 1;

	private static int chunkLoaderCounter = 1;

	public final Map<Long, Entity> updateEntities = new HashMap<>();

	public final Map<Long, BlockEntity> updateBlockEntities = new HashMap<>();

	public final java.util.Random rand = new java.util.Random();

	private final Map<Long, BlockEntity> blockEntities = new HashMap<>();

	private final Map<Long, Player> players = new HashMap<>();

	private final Map<Long, Entity> entities = new HashMap<>();

	// Use a weak map to avoid OOM
	private final ConcurrentMap<Object, Object> blockCache = CacheBuilder.newBuilder()
		.maximumSize(1000)
		.expireAfterWrite(10, TimeUnit.MINUTES)
		.build().asMap();

	// Use a weak map to avoid OOM
	private final ConcurrentMap<Object, Object> chunkCache = CacheBuilder.newBuilder()
		.maximumSize(1000)
		.expireAfterWrite(10, TimeUnit.MINUTES)
		.build().asMap();

	private final Server server;

	private final int levelId;

	private final Map<Integer, ChunkLoader> loaders = new HashMap<>();

	private final Map<Integer, Integer> loaderCounter = new HashMap<>();

	private final Map<Long, Map<Integer, ChunkLoader>> chunkLoaders = new HashMap<>();

	private final Map<Long, Map<Integer, Player>> playerLoaders = new HashMap<>();

	private final Map<Long, Long> unloadQueue = new HashMap<>();

	private final Map<Long, BaseFullChunk> chunks = new ConcurrentHashMap<>(); //temporal solution for CME

	// Storing the vector is redundant
	private final Object changeBlocksPresent = new Object();

	// Storing extra blocks past 512 is redundant
	private final Map<Short, Object> changeBlocksFullMap = new HashMap<Short, Object>()
	{

		@Override
		public int size()
		{
			return 32768;
		}
	};

	private final TreeSet<BlockUpdateEntry> updateQueue = new TreeSet<>();

	private final Map<Long, Map<Integer, Player>> chunkSendQueue = new HashMap<>();

	private final Map<Long, Boolean> chunkSendTasks = new HashMap<>();

	private final Map<Long, Boolean> chunkPopulationQueue = new HashMap<>();

	private final Map<Long, Boolean> chunkPopulationLock = new HashMap<>();

	private final Map<Long, Boolean> chunkGenerationQueue = new HashMap<>();

	private final Block[] blockStates;

	private final HashMap<Integer, Class<? extends Block>> randomTickBlocks = new HashMap<Integer, Class<? extends Block>>()
	{

		{
			put(Block.GRASS, BlockGrass.class);
			put(Block.FARMLAND, BlockFarmland.class);
			put(Block.MYCELIUM, BlockMycelium.class);

			put(Block.SAPLING, BlockSapling.class);

			put(Block.LEAVES, BlockLeaves.class);
			put(Block.LEAVES2, BlockLeaves2.class);

			put(Block.SNOW_LAYER, BlockSnowLayer.class);
			put(Block.ICE, BlockIce.class);
			put(Block.LAVA, BlockLava.class);
			put(Block.STILL_LAVA, BlockLavaStill.class);

			put(Block.CACTUS, BlockCactus.class);
			put(Block.BEETROOT_BLOCK, BlockBeetroot.class);
			put(Block.CARROT_BLOCK, BlockCarrot.class);
			put(Block.POTATO_BLOCK, BlockPotato.class);
			put(Block.MELON_STEM, BlockStemMelon.class);
			put(Block.PUMPKIN_STEM, BlockStemPumpkin.class);
			put(Block.WHEAT_BLOCK, BlockWheat.class);
			put(Block.SUGARCANE_BLOCK, BlockSugarcane.class);
			put(Block.RED_MUSHROOM, BlockMushroomRed.class);
			put(Block.BROWN_MUSHROOM, BlockMushroomBrown.class);
			put(Block.NETHER_WART_BLOCK, BlockNetherWart.class);

			put(Block.FIRE, BlockFire.class);
			put(Block.GLOWING_REDSTONE_ORE, BlockOreRedstoneGlowing.class);
			put(Block.COCOA_BLOCK, BlockCocoa.class);
		}
	};

	public boolean stopTime;

	//private final Map<BlockVector3, Integer> updateQueueIndex = new HashMap<>();
	public float skyLightSubtracted;

	public int sleepTicks = 0;

	public int tickRateTime = 0;

	public int tickRateCounter = 0;

	public GameRules gameRules;

	protected int updateLCG = (new Random()).nextInt();

	private boolean cacheChunks = false;

	private LevelProvider provider;

	private Map<Long, List<DataPacket>> chunkPackets = new HashMap<>();

	private float time;

	private String folderName;

	private Vector3 mutableBlock;

	// Avoid OOM, gc'd references result in whole chunk being sent (possibly higher cpu)
	private Map<Long, SoftReference<Map<Short, Object>>> changedBlocks = new HashMap<>();

	private int chunkGenerationQueueSize = 8;

	private int chunkPopulationQueueSize = 2;

	private boolean autoSave = true;

	private BlockMetadataStore blockMetadata;

	private boolean useSections;

	private Position temporalPosition;

	private Vector3 temporalVector;

	private int chunkTickRadius;

	private Map<Long, Integer> chunkTickList = new HashMap<>();

	private int chunksPerTicks;

	private boolean clearChunksOnTick;

	private int tickRate;

	private Class<? extends Generator> generator;

	private Generator generatorInstance;

	private boolean raining = false;

	private int rainTime = 0;

	private boolean thundering = false;

	private int thunderTime = 0;

	private long levelCurrentTick = 0;

	private int dimension;

	public Level(
		Server server,
		String name,
		String path,
		Class<? extends LevelProvider> provider
	)
	{
		this.blockStates = Block.fullList;
		this.levelId = levelIdCounter++;
		this.blockMetadata = new BlockMetadataStore(this);
		this.server = server;
		this.autoSave = server.getAutoSave();

		boolean convert = provider == McRegion.class || provider == LevelDB.class;
		try
		{
			if (convert)
			{
				String newPath = new File(path).getParent() + "/" + name + ".old/";
				new File(path).renameTo(new File(newPath));
				this.provider = provider.getConstructor(Level.class, String.class).newInstance(this, newPath);
			} else
			{
				this.provider = provider.getConstructor(Level.class, String.class).newInstance(this, path);
			}
		}
		catch (Exception e)
		{
			throw new LevelException("Caused by " + Utils.getExceptionMessage(e));
		}

		if (convert)
		{
			this.server.getLogger().info(this.server.getLanguage().translateString(
				"nukkit.level.updating",
				TextFormat.GREEN + this.provider.getName() + TextFormat.WHITE
			));
			LevelProvider old = this.provider;
			try
			{
				this.provider = new LevelProviderConverter(this, path)
					.from(old)
					.to(Anvil.class)
					.perform();
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
			old.close();
		}

		this.provider.updateLevelName(name);

		this.server.getLogger().info(this.server.getLanguage().translateString(
			"nukkit.level.preparing",
			this.provider.getName()
		));

		this.generator = Generator.getGenerator(this.provider.getGenerator());

		try
		{
			this.useSections = (boolean) provider.getMethod("usesChunkSection").invoke(null);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		this.folderName = name;
		this.time = this.provider.getTime();

		this.raining = this.provider.isRaining();
		this.rainTime = this.provider.getRainTime();
		if (this.rainTime <= 0)
		{
			setRainTime(rand.nextInt(168000) + 12000);
		}

		this.thundering = this.provider.isThundering();
		this.thunderTime = this.provider.getThunderTime();
		if (this.thunderTime <= 0)
		{
			setThunderTime(rand.nextInt(168000) + 12000);
		}

		this.levelCurrentTick = this.provider.getCurrentTick();

		this.chunkTickRadius = Math.min(
			this.server.getViewDistance(),
			Math.max(1, (Integer) this.server.getConfig("chunk-ticking.tick-radius", 4))
		);
		this.chunksPerTicks = (int) this.server.getConfig("chunk-ticking.per-tick", 40);
		this.chunkGenerationQueueSize = (int) this.server.getConfig("chunk-generation.queue-size", 8);
		this.chunkPopulationQueueSize = (int) this.server.getConfig("chunk-generation.population-queue-size", 2);
		this.chunkTickList.clear();
		this.clearChunksOnTick = (boolean) this.server.getConfig("chunk-ticking.clear-tick-list", true);
		this.cacheChunks = (boolean) this.server.getConfig("chunk-sending.cache-chunks", false);
		this.temporalPosition = new Position(0, 0, 0, this);
		this.temporalVector = new Vector3(0, 0, 0);
		this.tickRate = 1;

		this.skyLightSubtracted = this.calculateSkylightSubtracted(1);
	}

	public static long chunkHash(
		int x,
		int z
	)
	{
		return (((long) x) << 32) | (z & 0xffffffffL);
	}

	public static BlockVector3 blockHash(Vector3 block)
	{
		return blockHash(block.x, block.y, block.z);
	}

	public static short localBlockHash(
		double x,
		double y,
		double z
	)
	{
		byte hi = (byte) (((int) x & 15) + (((int) z & 15) << 4));
		byte lo = (byte) y;
		return (short) (((hi & 0xFF) << 8) | (lo & 0xFF));
	}

	public static Vector3 getBlockXYZ(
		long chunkHash,
		short blockHash
	)
	{
		int hi = (byte) (blockHash >>> 8);
		int lo = (byte) blockHash;
		int y = lo & 0xFF;
		int x = (hi & 0xF) + (getHashX(chunkHash) << 4);
		int z = ((hi >> 4) & 0xF) + (getHashZ(chunkHash) << 4);
		return new Vector3(x, y, z);
	}

	public static BlockVector3 blockHash(
		double x,
		double y,
		double z
	)
	{
		return new BlockVector3((int) x, (int) y, (int) z);
	}

	public static int chunkBlockHash(
		int x,
		int y,
		int z
	)
	{
		return (x << 12) | (z << 8) | y;
	}

	public static int getHashX(long hash)
	{
		return (int) (hash >> 32);
	}

	public static int getHashZ(long hash)
	{
		return (int) hash;
	}

	public static Vector3 getBlockXYZ(BlockVector3 hash)
	{
		return new Vector3(hash.x, hash.y, hash.z);
	}

	public static Chunk.Entry getChunkXZ(long hash)
	{
		return new Chunk.Entry(getHashX(hash), getHashZ(hash));
	}

	public static int generateChunkLoaderId(ChunkLoader loader)
	{
		if (loader.getLoaderId() == null || loader.getLoaderId() == 0)
		{
			return chunkLoaderCounter++;
		} else
		{
			throw new IllegalStateException("ChunkLoader has a loader id already assigned: " + loader.getLoaderId());
		}
	}

	public int getTickRate()
	{
		return tickRate;
	}

	public void setTickRate(int tickRate)
	{
		this.tickRate = tickRate;
	}

	public int getTickRateTime()
	{
		return tickRateTime;
	}

	public void initLevel()
	{
		try
		{
			this.generatorInstance = this.generator.getConstructor(Map.class)
				.newInstance(this.provider.getGeneratorOptions());
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		this.generatorInstance.init(this, new NukkitRandom(this.getSeed()));
		this.dimension = this.generatorInstance.getDimension();
		this.gameRules = this.provider.getGamerules();


		this.registerGenerator();
	}

	public void registerGenerator()
	{
		int size = this.server.getScheduler().getAsyncTaskPoolSize();
		for (int i = 0 ; i < size ; ++i)
		{
			this.server.getScheduler().scheduleAsyncTask(new GeneratorRegisterTask(this, this.generatorInstance));
		}
	}

	public void unregisterGenerator()
	{
		int size = this.server.getScheduler().getAsyncTaskPoolSize();
		for (int i = 0 ; i < size ; ++i)
		{
			this.server.getScheduler().scheduleAsyncTask(new GeneratorUnregisterTask(this));
		}
	}

	public BlockMetadataStore getBlockMetadata()
	{
		return this.blockMetadata;
	}

	public Server getServer()
	{
		return server;
	}

	final public LevelProvider getProvider()
	{
		return this.provider;
	}

	final public int getId()
	{
		return this.levelId;
	}

	public void close()
	{
		if (this.getAutoSave())
		{
			this.save();
		}

		for (BaseFullChunk chunk : new ArrayList<>(this.chunks.values()))
		{
			this.unloadChunk(chunk.getX(), chunk.getZ(), false);
		}

		this.unregisterGenerator();

		this.provider.close();
		this.provider = null;
		this.blockMetadata = null;
		this.blockCache.clear();
		this.temporalPosition = null;
		this.server.getLevels().remove(this.levelId);
	}

	public void addLevelEvent(
		Vector3 pos,
		int event
	)
	{
		this.addLevelEvent(pos, event, 0);
	}

	public void addLevelEvent(
		Vector3 pos,
		int event,
		int data
	)
	{
		LevelEventPacket packet = new LevelEventPacket();
		packet.evid = event;
		packet.x = (float) pos.x;
		packet.y = (float) pos.y;
		packet.z = (float) pos.z;
		packet.data = data;

		addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, packet);
	}

	public void addSound(Sound sound)
	{
		this.addSound(sound, (Player[]) null);
	}

	public void addSound(
		Sound sound,
		Player player
	)
	{
		this.addSound(sound, new Player[] { player });
	}

	public void addSound(
		Sound sound,
		Player[] players
	)
	{
		DataPacket[] packets = sound.encode();

		if (players == null)
		{
			if (packets != null)
			{
				for (DataPacket packet : packets)
				{
					this.addChunkPacket((int) sound.x >> 4, (int) sound.z >> 4, packet);
				}
			}
		} else
		{
			if (packets != null)
			{
				if (packets.length == 1)
				{
					Server.broadcastPacket(players, packets[0]);
				} else
				{
					this.server.batchPackets(players, packets, false);
				}
			}
		}
	}

	public void addSound(
		Sound sound,
		Collection<Player> players
	)
	{
		this.addSound(sound, players.stream().toArray(Player[]::new));
	}

	public void addLevelSoundEvent(
		byte type,
		int pitch,
		int data,
		Vector3 pos,
		boolean unknown,
		boolean disableRelativeVolume
	)
	{
		this.addLevelSoundEvent(type, pitch, data, pos, this.players.values(), unknown, disableRelativeVolume);
	}

	public void addLevelSoundEvent(
		byte type,
		int pitch,
		int data,
		Vector3 pos,
		Collection<Player> players,
		boolean unknown,
		boolean disableRelativeVolume
	)
	{
		LevelSoundEventPacket pk = new LevelSoundEventPacket();
		pk.sound = type;
		pk.pitch = pitch;
		pk.extraData = data;
		pk.x = (float) pos.x;
		pk.y = (float) pos.y;
		pk.z = (float) pos.z;
		pk.unknownBool = unknown;
		pk.disableRelativeVolume = disableRelativeVolume;

		if (players == null)
		{
			this.addChunkPacket(pos.getFloorX(), pos.getFloorZ(), pk);
		} else
		{
			Server.broadcastPacket(players, pk);
		}
	}

	public void addParticle(Particle particle)
	{
		this.addParticle(particle, (Player[]) null);
	}

	public void addParticle(
		Particle particle,
		Player player
	)
	{
		this.addParticle(particle, new Player[] { player });
	}

	public void addParticle(
		Particle particle,
		Player[] players
	)
	{
		DataPacket[] packets = particle.encode();

		if (players == null)
		{
			if (packets != null)
			{
				for (DataPacket packet : packets)
				{
					this.addChunkPacket((int) particle.x >> 4, (int) particle.z >> 4, packet);
				}
			}
		} else
		{
			if (packets != null)
			{
				if (packets.length == 1)
				{
					Server.broadcastPacket(players, packets[0]);
				} else
				{
					this.server.batchPackets(players, packets, false);
				}
			}
		}
	}

	public void addParticle(
		Particle particle,
		Collection<Player> players
	)
	{
		this.addParticle(particle, players.stream().toArray(Player[]::new));
	}

	public boolean getAutoSave()
	{
		return this.autoSave;
	}

	public void setAutoSave(boolean autoSave)
	{
		this.autoSave = autoSave;
	}

	public boolean unload()
	{
		return this.unload(false);
	}

	public boolean unload(boolean force)
	{
		LevelUnloadEvent ev = new LevelUnloadEvent(this);

		if (this == this.server.getDefaultLevel() && !force)
		{
			ev.setCancelled();
		}

		this.server.getPluginManager().callEvent(ev);

		if (!force && ev.isCancelled())
		{
			return false;
		}

		this.server.getLogger().info(this.server.getLanguage().translateString(
			"nukkit.level.unloading",
			TextFormat.GREEN + this.getName() + TextFormat.WHITE
		));
		Level defaultLevel = this.server.getDefaultLevel();

		for (Player player : new ArrayList<>(this.getPlayers().values()))
		{
			if (this == defaultLevel || defaultLevel == null)
			{
				player.close(player.getLeaveMessage(), "Forced default level unload");
			} else
			{
				player.teleport(this.server.getDefaultLevel().getSafeSpawn());
			}
		}

		if (this == defaultLevel)
		{
			this.server.setDefaultLevel(null);
		}

		this.close();

		return true;
	}

	public Map<Integer, Player> getChunkPlayers(
		int chunkX,
		int chunkZ
	)
	{
		Long index = Level.chunkHash(chunkX, chunkZ);
		if (this.playerLoaders.containsKey(index))
		{
			return new HashMap<>(this.playerLoaders.get(index));
		} else
		{
			return new HashMap<>();
		}
	}

	public ChunkLoader[] getChunkLoaders(
		int chunkX,
		int chunkZ
	)
	{
		Long index = Level.chunkHash(chunkX, chunkZ);
		if (this.chunkLoaders.containsKey(index))
		{
			return this.chunkLoaders.get(index).values().stream().toArray(ChunkLoader[]::new);
		} else
		{
			return new ChunkLoader[0];
		}
	}

	public void addChunkPacket(
		int chunkX,
		int chunkZ,
		DataPacket packet
	)
	{
		Long index = Level.chunkHash(chunkX, chunkZ);
		if (!this.chunkPackets.containsKey(index))
		{
			this.chunkPackets.put(index, new ArrayList<>());
		}
		this.chunkPackets.get(index).add(packet);
	}

	public void registerChunkLoader(
		ChunkLoader loader,
		int chunkX,
		int chunkZ
	)
	{
		this.registerChunkLoader(loader, chunkX, chunkZ, true);
	}

	public void registerChunkLoader(
		ChunkLoader loader,
		int chunkX,
		int chunkZ,
		boolean autoLoad
	)
	{
		int hash = loader.getLoaderId();
		Long index = Level.chunkHash(chunkX, chunkZ);
		if (!this.chunkLoaders.containsKey(index))
		{
			this.chunkLoaders.put(index, new HashMap<>());
			this.playerLoaders.put(index, new HashMap<>());
		} else if (this.chunkLoaders.get(index).containsKey(hash))
		{
			return;
		}

		this.chunkLoaders.get(index).put(hash, loader);
		if (loader instanceof Player)
		{
			this.playerLoaders.get(index).put(hash, (Player) loader);
		}

		if (!this.loaders.containsKey(hash))
		{
			this.loaderCounter.put(hash, 1);
			this.loaders.put(hash, loader);
		} else
		{
			this.loaderCounter.put(hash, this.loaderCounter.get(hash) + 1);
		}

		this.cancelUnloadChunkRequest(chunkX, chunkZ);

		if (autoLoad)
		{
			this.loadChunk(chunkX, chunkZ);
		}
	}

	public void unregisterChunkLoader(
		ChunkLoader loader,
		int chunkX,
		int chunkZ
	)
	{
		int hash = loader.getLoaderId();
		Long index = Level.chunkHash(chunkX, chunkZ);
		if (this.chunkLoaders.containsKey(index) && this.chunkLoaders.get(index).containsKey(hash))
		{
			this.chunkLoaders.get(index).remove(hash);
			this.playerLoaders.get(index).remove(hash);
			if (this.chunkLoaders.get(index).isEmpty())
			{
				this.chunkLoaders.remove(index);
				this.playerLoaders.remove(index);
				this.unloadChunkRequest(chunkX, chunkZ, true);
			}

			int count = this.loaderCounter.get(hash);
			if (--count == 0)
			{
				this.loaderCounter.remove(hash);
				this.loaders.remove(hash);
			} else
			{
				this.loaderCounter.put(hash, count);
			}
		}
	}

	public void checkTime()
	{
		if (!this.stopTime)
		{
			this.time += tickRate;
		}
	}

	public void sendTime(Player player)
	{
		if (this.stopTime)
		{
			SetTimePacket pk0 = new SetTimePacket();
			pk0.time = (int) this.time;
			player.dataPacket(pk0);
		}

		SetTimePacket pk = new SetTimePacket();
		pk.time = (int) this.time;

		player.dataPacket(pk);
	}

	public void sendTime()
	{
		if (this.stopTime)
		{
			SetTimePacket pk0 = new SetTimePacket();
			pk0.time = (int) this.time;
			Server.broadcastPacket(this.players.values().stream().toArray(Player[]::new), pk0);
		}

		SetTimePacket pk = new SetTimePacket();
		pk.time = (int) this.time;

		Server.broadcastPacket(this.players.values().stream().toArray(Player[]::new), pk);
	}

	public GameRules getGameRules()
	{
		return gameRules;
	}

	public void doTick(int currentTick)
	{

		this.checkTime();

		// Tick Weather
		this.rainTime--;
		if (this.rainTime <= 0)
		{
			if (!this.setRaining(!this.raining))
			{
				if (this.raining)
				{
					setRainTime(rand.nextInt(12000) + 12000);
				} else
				{
					setRainTime(rand.nextInt(168000) + 12000);
				}
			}
		}

		this.thunderTime--;
		if (this.thunderTime <= 0)
		{
			if (!this.setThundering(!this.thundering))
			{
				if (this.thundering)
				{
					setThunderTime(rand.nextInt(12000) + 3600);
				} else
				{
					setThunderTime(rand.nextInt(168000) + 12000);
				}
			}
		}

		if (this.isThundering())
		{
			synchronized (this)
			{
				for (Map.Entry<Long, BaseFullChunk> entry : this.chunks.entrySet())
				{
					long index = entry.getKey();
					BaseFullChunk chunk = entry.getValue();
					if (rand.nextInt(10000) == 0)
					{
						this.updateLCG = this.updateLCG * 3 + 1013904223;
						int LCG = this.updateLCG >> 2;

						int chunkX = chunk.getX() * 16;
						int chunkZ = chunk.getZ() * 16;
						Vector3 vector = this.adjustPosToNearbyEntity(new Vector3(chunkX + (LCG & 15), 0, chunkZ + (LCG >> 8 & 15)));

						int bId = this.getBlockIdAt(vector.getFloorX(), vector.getFloorY(), vector.getFloorZ());
						if (bId != Block.TALL_GRASS && bId != Block.WATER)
							vector.y += 1;
						CompoundTag nbt = new CompoundTag()
							.putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", vector.x))
								.add(new DoubleTag("", vector.y)).add(new DoubleTag("", vector.z)))
							.putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0))
								.add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
							.putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", 0))
								.add(new FloatTag("", 0)));

						EntityLightning bolt = new EntityLightning(chunk, nbt);
						LightningStrikeEvent ev = new LightningStrikeEvent(this, bolt);
						getServer().getPluginManager().callEvent(ev);
						if (!ev.isCancelled())
						{
							bolt.spawnToAll();
						} else
						{
							bolt.setEffect(false);
						}

						this.addLevelSoundEvent(LevelSoundEventPacket.SOUND_THUNDER, 93, -1, vector, false, false);
						this.addLevelSoundEvent(LevelSoundEventPacket.SOUND_EXPLODE, 93, -1, vector, false, false);
					}

				}
			}

		}

		this.skyLightSubtracted = this.calculateSkylightSubtracted(1);

		this.levelCurrentTick++;

		this.unloadChunks();
		List<BlockUpdateEntry> toSchedule = new ArrayList<>();

		for (int i = 0 ; i < this.updateQueue.size() ; i++)
		{
			BlockUpdateEntry entry = this.updateQueue.first();

			if (entry.delay > this.getCurrentTick())
			{
				break;
			}

			if (isAreaLoaded(new AxisAlignedBB(entry.pos, entry.pos)))
			{
				Block block = this.getBlock(entry.pos);

				if (Block.equals(block, entry.block, false))
				{
					block.onUpdate(BLOCK_UPDATE_SCHEDULED);
				}
			} else
			{
				toSchedule.add(entry);
			}
			this.updateQueue.remove(entry);
		}

		for (BlockUpdateEntry entry : toSchedule)
		{
			this.scheduleUpdate(entry.block, entry.pos, 0);
		}

		if (!this.updateEntities.isEmpty())
		{
			for (long id : new ArrayList<>(this.updateEntities.keySet()))
			{
				Entity entity = this.updateEntities.get(id);
				if (entity.closed || !entity.onUpdate(currentTick))
				{
					this.updateEntities.remove(id);
				}
			}
		}

		if (!this.updateBlockEntities.isEmpty())
		{
			for (long id : new ArrayList<>(this.updateBlockEntities.keySet()))
			{
				if (!this.updateBlockEntities.get(id).onUpdate())
				{
					this.updateBlockEntities.remove(id);
				}
			}
		}

		this.tickChunks();

		if (!this.changedBlocks.isEmpty())
		{
			if (!this.players.isEmpty())
			{
				for (Map.Entry<Long, SoftReference<Map<Short, Object>>> entry : changedBlocks.entrySet())
				{
					long index = entry.getKey();
					Map<Short, Object> blocks = entry.getValue().get();
					this.chunkCache.remove(index);
					int chunkX = Level.getHashX(index);
					int chunkZ = Level.getHashZ(index);
					if (blocks == null || blocks.size() > MAX_BLOCK_CACHE)
					{
						FullChunk chunk = this.getChunk(chunkX, chunkZ);
						for (Player p : this.getChunkPlayers(chunkX, chunkZ).values())
						{
							p.onChunkChanged(chunk);
						}
					} else
					{
						Collection<Player> toSend = this.getChunkPlayers(chunkX, chunkZ).values();
						Player[] playerArray = toSend.toArray(new Player[toSend.size()]);
						Vector3[] blocksArray = new Vector3[blocks.size()];
						int i = 0;
						for (short blockHash : blocks.keySet())
						{
							blocksArray[i++] = getBlockXYZ(index, blockHash);
						}
						this.sendBlocks(playerArray, blocksArray, UpdateBlockPacket.FLAG_ALL);
					}
				}
			} else
			{
				this.chunkCache.clear();
			}

			this.changedBlocks.clear();
		}

		this.processChunkRequest();

		if (this.sleepTicks > 0 && --this.sleepTicks <= 0)
		{
			this.checkSleep();
		}

		for (long index : this.chunkPackets.keySet())
		{
			int chunkX = Level.getHashX(index);
			int chunkZ = Level.getHashZ(index);
			Player[] chunkPlayers = this.getChunkPlayers(chunkX, chunkZ).values().stream().toArray(Player[]::new);
			if (chunkPlayers.length > 0)
			{
				for (DataPacket pk : this.chunkPackets.get(index))
				{
					Server.broadcastPacket(chunkPlayers, pk);
				}
			}
		}

		this.chunkPackets.clear();
	}

	public Vector3 adjustPosToNearbyEntity(Vector3 pos)
	{
		pos.y = this.getHighestBlockAt(pos.getFloorX(), pos.getFloorZ());
		AxisAlignedBB axisalignedbb = new AxisAlignedBB(pos.x, pos.y, pos.z, pos.getX(), 255, pos.getZ()).expand(3, 3, 3);
		List<Entity> list = new ArrayList<>();

		for (Entity entity : this.getCollidingEntities(axisalignedbb))
		{
			if (entity.isAlive() && canBlockSeeSky(entity))
			{
				list.add(entity);
			}
		}

		if (!list.isEmpty())
		{
			return list.get(this.rand.nextInt(list.size())).getPosition();
		} else
		{
			if (pos.getY() == -1)
			{
				pos = pos.up(2);
			}

			return pos;
		}
	}

	public void checkSleep()
	{
		if (this.players.isEmpty())
		{
			return;
		}

		boolean resetTime = true;
		for (Player p : this.getPlayers().values())
		{
			if (!p.isSleeping())
			{
				resetTime = false;
				break;
			}
		}

		if (resetTime)
		{
			int time = this.getTime() % Level.TIME_FULL;

			if (time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE)
			{
				this.setTime(this.getTime() + Level.TIME_FULL - time);

				for (Player p : this.getPlayers().values())
				{
					p.stopSleep();
				}
			}
		}
	}

	public void sendBlockExtraData(
		int x,
		int y,
		int z,
		int id,
		int data
	)
	{
		this.sendBlockExtraData(x, y, z, id, data, this.getChunkPlayers(x >> 4, z >> 4).values());
	}

	public void sendBlockExtraData(
		int x,
		int y,
		int z,
		int id,
		int data,
		Player[] players
	)
	{
		LevelEventPacket pk = new LevelEventPacket();
		pk.evid = LevelEventPacket.EVENT_SET_DATA;
		pk.x = x + 0.5f;
		pk.y = y + 0.5f;
		pk.z = z + 0.5f;
		pk.data = (data << 8) | id;

		Server.broadcastPacket(players, pk);
	}

	public void sendBlockExtraData(
		int x,
		int y,
		int z,
		int id,
		int data,
		Collection<Player> players
	)
	{
		LevelEventPacket pk = new LevelEventPacket();
		pk.evid = LevelEventPacket.EVENT_SET_DATA;
		pk.x = x + 0.5f;
		pk.y = y + 0.5f;
		pk.z = z + 0.5f;
		pk.data = (data << 8) | id;

		Server.broadcastPacket(players, pk);
	}

	public void sendBlocks(
		Player[] target,
		Vector3[] blocks
	)
	{
		this.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_NONE);
	}

	public void sendBlocks(
		Player[] target,
		Vector3[] blocks,
		int flags
	)
	{
		this.sendBlocks(target, blocks, flags, false);
	}

	public void sendBlocks(
		Player[] target,
		Vector3[] blocks,
		int flags,
		boolean optimizeRebuilds
	)
	{
		List<UpdateBlockPacket> packets = new ArrayList<>();
		if (optimizeRebuilds)
		{
			Map<Long, Boolean> chunks = new HashMap<>();
			for (Vector3 b : blocks)
			{
				if (b == null)
				{
					continue;
				}
				boolean first = false;

				long index = Level.chunkHash((int) b.x >> 4, (int) b.z >> 4);
				if (!chunks.containsKey(index))
				{
					chunks.put(index, true);
					first = true;
				}

				if (b instanceof Block)
				{
					UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
					updateBlockPacket.x = (int) ((Block) b).x;
					updateBlockPacket.y = (int) ((Block) b).y;
					updateBlockPacket.z = (int) ((Block) b).z;
					updateBlockPacket.blockId = ((Block) b).getId();
					updateBlockPacket.blockData = ((Block) b).getDamage();
					updateBlockPacket.flags = first ? flags : UpdateBlockPacket.FLAG_NONE;
					packets.add(updateBlockPacket);
				} else
				{
					int fullBlock = this.getFullBlock((int) b.x, (int) b.y, (int) b.z);
					UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
					updateBlockPacket.x = (int) b.x;
					updateBlockPacket.y = (int) b.y;
					updateBlockPacket.z = (int) b.z;
					updateBlockPacket.blockId = fullBlock >> 4;
					updateBlockPacket.blockData = fullBlock & 0xf;
					updateBlockPacket.flags = first ? flags : UpdateBlockPacket.FLAG_NONE;
					packets.add(updateBlockPacket);
				}
			}
		} else
		{
			for (Vector3 b : blocks)
			{
				if (b == null)
				{
					continue;
				}
				UpdateBlockPacket packet = new UpdateBlockPacket();
				if (b instanceof Block)
				{
					UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
					updateBlockPacket.x = (int) ((Block) b).x;
					updateBlockPacket.y = (int) ((Block) b).y;
					updateBlockPacket.z = (int) ((Block) b).z;
					updateBlockPacket.blockId = ((Block) b).getId();
					updateBlockPacket.blockData = ((Block) b).getDamage();
					updateBlockPacket.flags = flags;
					packets.add(updateBlockPacket);
				} else
				{
					int fullBlock = this.getFullBlock((int) b.x, (int) b.y, (int) b.z);
					UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
					updateBlockPacket.x = (int) b.x;
					updateBlockPacket.y = (int) b.y;
					updateBlockPacket.z = (int) b.z;
					updateBlockPacket.blockId = fullBlock >> 4;
					updateBlockPacket.blockData = fullBlock & 0xf;
					updateBlockPacket.flags = flags;
					packets.add(updateBlockPacket);
				}
				packets.add(packet);
			}
		}
		this.server.batchPackets(target, packets.toArray(new DataPacket[packets.size()]));
	}

	public void clearCache()
	{
		this.clearCache(false);
	}

	public void clearCache(boolean full)
	{
		if (full)
		{
			this.chunkCache.clear();
			this.blockCache.clear();
		} else
		{
			if (this.chunkCache.size() > 2048)
			{
				this.chunkCache.clear();
			}

			if (this.blockCache.size() > 2048)
			{
				this.blockCache.clear();
			}
		}
	}

	public void clearChunkCache(
		int chunkX,
		int chunkZ
	)
	{
		this.chunkCache.remove(Level.chunkHash(chunkX, chunkZ));
	}

	private void tickChunks()
	{
		if (this.chunksPerTicks <= 0 || this.loaders.isEmpty())
		{
			this.chunkTickList = new HashMap<>();
			return;
		}

		int chunksPerLoader = Math.min(200, Math.max(1, (int) (((double) (this.chunksPerTicks - this.loaders.size()) / this.loaders.size() + 0.5))));
		int randRange = 3 + chunksPerLoader / 30;
		randRange = randRange > this.chunkTickRadius ? this.chunkTickRadius : randRange;

		for (ChunkLoader loader : this.loaders.values())
		{
			int chunkX = (int) loader.getX() >> 4;
			int chunkZ = (int) loader.getZ() >> 4;

			Long index = Level.chunkHash(chunkX, chunkZ);
			int existingLoaders = Math.max(0, this.chunkTickList.containsKey(index) ? this.chunkTickList.get(index) : 0);
			this.chunkTickList.put(index, existingLoaders + 1);
			for (int chunk = 0 ; chunk < chunksPerLoader ; ++chunk)
			{
				int dx = new java.util.Random().nextInt(2 * randRange) - randRange;
				int dz = new java.util.Random().nextInt(2 * randRange) - randRange;
				long hash = Level.chunkHash(dx + chunkX, dz + chunkZ);
				if (!this.chunkTickList.containsKey(hash) && this.chunks.containsKey(hash))
				{
					this.chunkTickList.put(hash, -1);
				}
			}
		}

		int blockTest = 0;

		for (Long index : new ArrayList<>(this.chunkTickList.keySet()))
		{
			int loaders = this.chunkTickList.get(index);

			int chunkX = getHashX(index);
			int chunkZ = getHashZ(index);

			FullChunk chunk;
			if (!this.chunks.containsKey(index) || (chunk = this.getChunk(chunkX, chunkZ, false)) == null)
			{
				this.chunkTickList.remove(index);
				continue;
			} else if (loaders <= 0)
			{
				this.chunkTickList.remove(index);
			}

			for (Entity entity : chunk.getEntities().values())
			{
				entity.scheduleUpdate();
			}
			int tickSpeed = this.gameRules.getInt("randomTickSpeed");

			if (tickSpeed > 0)
			{
				int blockId;
				if (this.useSections)
				{
					for (ChunkSection section : ((Chunk) chunk).getSections())
					{
						if (!(section instanceof EmptyChunkSection))
						{
							int Y = section.getY();
							this.updateLCG = this.updateLCG * 3 + 1013904223;
							int k = this.updateLCG >> 2;
							for (int i = 0 ; i < tickSpeed ; ++i, k >>= 10)
							{
								int x = k & 0x0f;
								int y = k >> 8 & 0x0f;
								int z = k >> 16 & 0x0f;

								blockId = section.getBlockId(x, y, z);
								if (this.randomTickBlocks.containsKey(blockId))
								{
									Class<? extends Block> clazz = this.randomTickBlocks.get(blockId);
									try
									{
										Block block = clazz.getConstructor(int.class).newInstance(section.getBlockData(x, y, z));
										block.x = chunkX * 16 + x;
										block.y = (Y << 4) + y;
										block.z = chunkZ * 16 + z;
										block.level = this;
										block.onUpdate(BLOCK_UPDATE_RANDOM);
									}
									catch (Exception e)
									{
										throw new RuntimeException(e);
									}
								}
							}
						}
					}
				} else
				{
					for (int Y = 0 ; Y < 8 && (Y < 3 || blockTest != 0) ; ++Y)
					{
						blockTest = 0;
						this.updateLCG = this.updateLCG * 3 + 1013904223;
						int k = this.updateLCG >> 2;
						for (int i = 0 ; i < tickSpeed ; ++i, k >>= 10)
						{
							int x = k & 0x0f;
							int y = k >> 8 & 0x0f;
							int z = k >> 16 & 0x0f;

							blockTest |= blockId = chunk.getBlockId(x, y + (Y << 4), z);
							if (this.randomTickBlocks.containsKey(blockId))
							{
								Class<? extends Block> clazz = this.randomTickBlocks.get(blockId);

								Block block;
								try
								{
									block = clazz.getConstructor(int.class).newInstance(chunk.getBlockData(x, y + (Y << 4), z));
								}
								catch (Exception e)
								{
									throw new RuntimeException(e);
								}
								block.x = chunkX * 16 + x;
								block.y = (Y << 4) + y;
								block.z = chunkZ * 16 + z;
								block.level = this;
								block.onUpdate(BLOCK_UPDATE_RANDOM);
							}
						}
					}
				}
			}
		}

		if (this.clearChunksOnTick)
		{
			this.chunkTickList = new HashMap<>();
		}
	}

	public boolean save()
	{
		return this.save(false);
	}

	public boolean save(boolean force)
	{
		if (!this.getAutoSave() && !force)
		{
			return false;
		}

		this.server.getPluginManager().callEvent(new LevelSaveEvent(this));

		this.provider.setTime((int) this.time);
		this.provider.setRaining(this.raining);
		this.provider.setRainTime(this.rainTime);
		this.provider.setThundering(this.thundering);
		this.provider.setThunderTime(this.thunderTime);
		this.provider.setCurrentTick(this.levelCurrentTick);
		this.saveChunks();
		if (this.provider instanceof BaseLevelProvider)
		{
			this.provider.saveLevelData();
		}

		return true;
	}

	public void saveChunks()
	{
		for (FullChunk chunk : new ArrayList<>(this.chunks.values()))
		{
			if (chunk.hasChanged())
			{
				try
				{
					this.provider.setChunk(chunk.getX(), chunk.getZ(), chunk);
					this.provider.saveChunk(chunk.getX(), chunk.getZ());

					chunk.setChanged(false);
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		}
	}

	public void updateAroundRedstone(
		Vector3 pos,
		BlockFace face
	)
	{
		for (BlockFace side : BlockFace.values())
		{
            /*if(face != null && side == face) {
                continue;
            }*/

			this.getBlock(pos.getSide(side)).onUpdate(BLOCK_UPDATE_REDSTONE);
		}
	}

	public void updateComparatorOutputLevel(Vector3 v)
	{
		for (BlockFace face : Plane.HORIZONTAL)
		{
			Vector3 pos = v.getSide(face);

			if (this.isChunkLoaded((int) pos.x >> 4, (int) pos.z >> 4))
			{
				Block block1 = this.getBlock(pos);

				if (BlockRedstoneDiode.isDiode(block1))
				{
					block1.onUpdate(BLOCK_UPDATE_REDSTONE);
				} else if (block1.isNormalBlock())
				{
					pos = pos.getSide(face);
					block1 = this.getBlock(pos);

					if (BlockRedstoneDiode.isDiode(block1))
					{
						block1.onUpdate(BLOCK_UPDATE_REDSTONE);
					}
				}
			}
		}
	}

	public void updateAround(Vector3 pos)
	{
		BlockUpdateEvent ev;

		for (BlockFace face : BlockFace.values())
		{
			this.server.getPluginManager().callEvent(
				ev = new BlockUpdateEvent(this.getBlock(pos.getSide(face))));
			if (!ev.isCancelled())
			{
				ev.getBlock().onUpdate(BLOCK_UPDATE_NORMAL);
			}
		}
	}

	public void scheduleUpdate(
		Block pos,
		int delay
	)
	{
		this.scheduleUpdate(pos, pos, delay, 0, true);
	}

	public void scheduleUpdate(
		Block block,
		Vector3 pos,
		int delay
	)
	{
		this.scheduleUpdate(block, pos, delay, 0, true);
	}

	public void scheduleUpdate(
		Block block,
		Vector3 pos,
		int delay,
		int priority
	)
	{
		this.scheduleUpdate(block, pos, delay, priority, true);
	}

	public void scheduleUpdate(
		Block block,
		Vector3 pos,
		int delay,
		int priority,
		boolean checkArea
	)
	{
		if (block.getId() == 0 || (checkArea && !this.isChunkLoaded(block.getFloorX() >> 4, block.getFloorZ() >> 4)))
		{
			return;
		}

		BlockUpdateEntry entry = new BlockUpdateEntry(pos.floor(), block, ((long) delay) + getCurrentTick(), priority);

		if (!this.updateQueue.contains(entry))
		{
			this.updateQueue.add(entry);
		}
	}

	public boolean cancelSheduledUpdate(
		Vector3 pos,
		Block block
	)
	{
		BlockUpdateEntry entry = new BlockUpdateEntry(pos, block);

		return this.updateQueue.remove(entry);
	}

	public boolean isUpdateScheduled(
		Vector3 pos,
		Block block
	)
	{
		BlockUpdateEntry entry = new BlockUpdateEntry(pos, block);

		return this.updateQueue.contains(entry);
	}

	public List<BlockUpdateEntry> getPendingBlockUpdates(FullChunk chunk)
	{
		int minX = (chunk.getX() << 4) - 2;
		int maxX = minX + 16 + 2;
		int minZ = (chunk.getZ() << 4) - 2;
		int maxZ = minZ + 16 + 2;

		return this.getPendingBlockUpdates(new AxisAlignedBB(minX, 0, minZ, maxX, 256, maxZ));
	}

	public List<BlockUpdateEntry> getPendingBlockUpdates(AxisAlignedBB boundingBox)
	{
		List<BlockUpdateEntry> list = null;

		Iterator<BlockUpdateEntry> iterator;

		iterator = this.updateQueue.iterator();

		while (iterator.hasNext())
		{
			BlockUpdateEntry entry = iterator.next();
			Vector3 pos = entry.pos;

			if (pos.getX() >= boundingBox.minX && pos.getX() < boundingBox.maxX && pos.getZ() >= boundingBox.minZ && pos.getZ() < boundingBox.maxZ)
			{
				if (list == null)
				{
					list = new ArrayList<>();
				}

				list.add(entry);
			}
		}

		return list;
	}

	public Block[] getCollisionBlocks(AxisAlignedBB bb)
	{
		return this.getCollisionBlocks(bb, false);
	}

	public Block[] getCollisionBlocks(
		AxisAlignedBB bb,
		boolean targetFirst
	)
	{
		int minX = NukkitMath.floorDouble(bb.minX);
		int minY = NukkitMath.floorDouble(bb.minY);
		int minZ = NukkitMath.floorDouble(bb.minZ);
		int maxX = NukkitMath.ceilDouble(bb.maxX);
		int maxY = NukkitMath.ceilDouble(bb.maxY);
		int maxZ = NukkitMath.ceilDouble(bb.maxZ);

		List<Block> collides = new ArrayList<>();

		if (targetFirst)
		{
			for (int z = minZ ; z <= maxZ ; ++z)
			{
				for (int x = minX ; x <= maxX ; ++x)
				{
					for (int y = minY ; y <= maxY ; ++y)
					{
						Block block = this.getBlock(this.temporalVector.setComponents(x, y, z));
						if (block.getId() != 0 && block.collidesWithBB(bb))
						{
							return new Block[] { block };
						}
					}
				}
			}
		} else
		{
			for (int z = minZ ; z <= maxZ ; ++z)
			{
				for (int x = minX ; x <= maxX ; ++x)
				{
					for (int y = minY ; y <= maxY ; ++y)
					{
						Block block = this.getBlock(this.temporalVector.setComponents(x, y, z));
						if (block.getId() != 0 && block.collidesWithBB(bb))
						{
							collides.add(block);
						}
					}
				}
			}
		}

		return collides.stream().toArray(Block[]::new);
	}

	public boolean isFullBlock(Vector3 pos)
	{
		AxisAlignedBB bb;
		if (pos instanceof Block)
		{
			if (((Block) pos).isSolid())
			{
				return true;
			}
			bb = ((Block) pos).getBoundingBox();
		} else
		{
			bb = this.getBlock(pos).getBoundingBox();
		}

		return bb != null && bb.getAverageEdgeLength() >= 1;
	}

	public AxisAlignedBB[] getCollisionCubes(
		Entity entity,
		AxisAlignedBB bb
	)
	{
		return this.getCollisionCubes(entity, bb, true);
	}

	public AxisAlignedBB[] getCollisionCubes(
		Entity entity,
		AxisAlignedBB bb,
		boolean entities
	)
	{
		int minX = NukkitMath.floorDouble(bb.minX);
		int minY = NukkitMath.floorDouble(bb.minY);
		int minZ = NukkitMath.floorDouble(bb.minZ);
		int maxX = NukkitMath.ceilDouble(bb.maxX);
		int maxY = NukkitMath.ceilDouble(bb.maxY);
		int maxZ = NukkitMath.ceilDouble(bb.maxZ);

		List<AxisAlignedBB> collides = new ArrayList<>();

		for (int z = minZ ; z <= maxZ ; ++z)
		{
			for (int x = minX ; x <= maxX ; ++x)
			{
				for (int y = minY ; y <= maxY ; ++y)
				{
					Block block = this.getBlock(this.temporalVector.setComponents(x, y, z));
					if (!block.canPassThrough() && block.collidesWithBB(bb))
					{
						collides.add(block.getBoundingBox());
					}
				}
			}
		}

		if (entities)
		{
			for (Entity ent : this.getCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity))
			{
				collides.add(ent.boundingBox.clone());
			}
		}

		return collides.stream().toArray(AxisAlignedBB[]::new);
	}

	public boolean hasCollision(
		Entity entity,
		AxisAlignedBB bb,
		boolean entities
	)
	{
		int minX = NukkitMath.floorDouble(bb.minX);
		int minY = NukkitMath.floorDouble(bb.minY);
		int minZ = NukkitMath.floorDouble(bb.minZ);
		int maxX = NukkitMath.ceilDouble(bb.maxX);
		int maxY = NukkitMath.ceilDouble(bb.maxY);
		int maxZ = NukkitMath.ceilDouble(bb.maxZ);

		for (int z = minZ ; z <= maxZ ; ++z)
		{
			for (int x = minX ; x <= maxX ; ++x)
			{
				for (int y = minY ; y <= maxY ; ++y)
				{
					Block block = this.getBlock(this.temporalVector.setComponents(x, y, z));
					if (!block.canPassThrough() && block.collidesWithBB(bb))
					{
						return true;
					}
				}
			}
		}

		if (entities)
		{
			return this.getCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity).length > 0;
		}
		return false;
	}

	public int getFullLight(Vector3 pos)
	{
		FullChunk chunk = this.getChunk((int) pos.x >> 4, (int) pos.z >> 4, false);
		int level = 0;
		if (chunk != null)
		{
			level = chunk.getBlockSkyLight((int) pos.x & 0x0f, (int) pos.y & 0xff, (int) pos.z & 0x0f);
			level -= this.skyLightSubtracted;

			if (level < 15)
			{
				level = Math.max(
					chunk.getBlockLight((int) pos.x & 0x0f, (int) pos.y & 0xff, (int) pos.z & 0x0f),
					level
				);
			}
		}

		return level;
	}

	public int calculateSkylightSubtracted(float tickDiff)
	{
		float angle = this.calculateCelestialAngle(getTime(), tickDiff);
		float light = 1 - (MathHelper.cos(angle * ((float) Math.PI * 2F)) * 2 + 0.5f);
		light = light < 0 ? 0 : light > 1 ? 1 : light;
		light = 1 - light;
		light = (float) ((double) light * ((isRaining() ? 1 : 0) - (double) 5f / 16d));
		light = (float) ((double) light * ((isThundering() ? 1 : 0) - (double) 5f / 16d));
		light = 1 - light;
		return (int) (light * 11f);
	}

	public float calculateCelestialAngle(
		int time,
		float tickDiff
	)
	{
		float angle = ((float) time + tickDiff) / 24000f - 0.25f;

		if (angle < 0)
		{
			++angle;
		}

		if (angle > 1)
		{
			--angle;
		}

		float i = 1 - (float) ((Math.cos((double) angle * Math.PI) + 1) / 2d);
		angle = angle + (i - angle) / 3;
		return angle;
	}

	public int getMoonPhase(long worldTime)
	{
		return (int) (worldTime / 24000 % 8 + 8) % 8;
	}

	public int getFullBlock(
		int x,
		int y,
		int z
	)
	{
		return this.getChunk(x >> 4, z >> 4, false).getFullBlock(x & 0x0f, y & 0xff, z & 0x0f);
	}

	public Block getBlock(Vector3 pos)
	{
		return this.getBlock(pos, true);
	}

	public Block getBlock(
		Vector3 pos,
		boolean cached
	)
	{
		long chunkIndex = Level.chunkHash((int) pos.x >> 4, (int) pos.z >> 4);
		BlockVector3 index = Level.blockHash((int) pos.x, (int) pos.y, (int) pos.z);
		int fullState;

		Block block;
		BaseFullChunk chunk;

		if (cached && (block = (Block) this.blockCache.get(index)) != null)
		{
			return block;
		} else if (pos.y >= 0 && pos.y < 256 && (chunk = this.chunks.get(chunkIndex)) != null)
		{
			fullState = chunk.getFullBlock((int) pos.x & 0x0f, (int) pos.y & 0xff,
				(int) pos.z & 0x0f
			);
		} else
		{
			fullState = 0;
		}

		block = this.blockStates[fullState & 0xfff].clone();

		block.x = pos.x;
		block.y = pos.y;
		block.z = pos.z;
		block.level = this;

		this.blockCache.put(index, block);

		return block;
	}

	public void updateAllLight(Vector3 pos)
	{
		this.updateBlockSkyLight((int) pos.x, (int) pos.y, (int) pos.z); //TODO: check if dimension has sky
		this.updateBlockLight((int) pos.x, (int) pos.y, (int) pos.z);
	}

	public void updateBlockSkyLight(
		int x,
		int y,
		int z
	)
	{
		//TODO: sky light
	}

	public void updateBlockLight(
		int x,
		int y,
		int z
	)
	{
		Queue<Vector3> lightPropagationQueue = new ConcurrentLinkedQueue<>();
		Queue<Object[]> lightRemovalQueue = new ConcurrentLinkedQueue<>();
		Map<BlockVector3, Boolean> visited = new HashMap<>();
		Map<BlockVector3, Boolean> removalVisited = new HashMap<>();

		int oldLevel = this.getBlockLightAt(x, y, z);
		int newLevel = Block.light[this.getBlockIdAt(x, y, z)];

		if (oldLevel != newLevel)
		{
			this.setBlockLightAt(x, y, z, newLevel);

			if (newLevel < oldLevel)
			{
				removalVisited.put(Level.blockHash(x, y, z), true);
				lightRemovalQueue.add(new Object[] { new Vector3(x, y, z), oldLevel });
			} else
			{
				visited.put(Level.blockHash(x, y, z), true);
				lightPropagationQueue.add(new Vector3(x, y, z));
			}
		}

		while (!lightRemovalQueue.isEmpty())
		{
			Object[] val = lightRemovalQueue.poll();
			Vector3 node = (Vector3) val[0];
			int lightLevel = (int) val[1];

			this.computeRemoveBlockLight((int) node.x - 1, (int) node.y, (int) node.z, lightLevel, lightRemovalQueue,
				lightPropagationQueue, removalVisited, visited
			);
			this.computeRemoveBlockLight((int) node.x + 1, (int) node.y, (int) node.z, lightLevel, lightRemovalQueue,
				lightPropagationQueue, removalVisited, visited
			);
			this.computeRemoveBlockLight((int) node.x, (int) node.y - 1, (int) node.z, lightLevel, lightRemovalQueue,
				lightPropagationQueue, removalVisited, visited
			);
			this.computeRemoveBlockLight((int) node.x, (int) node.y + 1, (int) node.z, lightLevel, lightRemovalQueue,
				lightPropagationQueue, removalVisited, visited
			);
			this.computeRemoveBlockLight((int) node.x, (int) node.y, (int) node.z - 1, lightLevel, lightRemovalQueue,
				lightPropagationQueue, removalVisited, visited
			);
			this.computeRemoveBlockLight((int) node.x, (int) node.y, (int) node.z + 1, lightLevel, lightRemovalQueue,
				lightPropagationQueue, removalVisited, visited
			);
		}

		while (!lightPropagationQueue.isEmpty())
		{
			Vector3 node = lightPropagationQueue.poll();
			int lightLevel = this.getBlockLightAt((int) node.x, (int) node.y, (int) node.z)
			                 - Block.lightFilter[this.getBlockIdAt((int) node.x, (int) node.y, (int) node.z)];

			if (lightLevel >= 1)
			{
				this.computeSpreadBlockLight((int) node.x - 1, (int) node.y, (int) node.z, lightLevel,
					lightPropagationQueue, visited
				);
				this.computeSpreadBlockLight((int) node.x + 1, (int) node.y, (int) node.z, lightLevel,
					lightPropagationQueue, visited
				);
				this.computeSpreadBlockLight((int) node.x, (int) node.y - 1, (int) node.z, lightLevel,
					lightPropagationQueue, visited
				);
				this.computeSpreadBlockLight((int) node.x, (int) node.y + 1, (int) node.z, lightLevel,
					lightPropagationQueue, visited
				);
				this.computeSpreadBlockLight((int) node.x, (int) node.y, (int) node.z - 1, lightLevel,
					lightPropagationQueue, visited
				);
				this.computeSpreadBlockLight((int) node.x, (int) node.y, (int) node.z + 1, lightLevel,
					lightPropagationQueue, visited
				);
			}
		}
	}

	private void computeRemoveBlockLight(
		int x,
		int y,
		int z,
		int currentLight,
		Queue<Object[]> queue,
		Queue<Vector3> spreadQueue,
		Map<BlockVector3, Boolean> visited,
		Map<BlockVector3, Boolean> spreadVisited
	)
	{
		int current = this.getBlockLightAt(x, y, z);
		BlockVector3 index = Level.blockHash(x, y, z);
		if (current != 0 && current < currentLight)
		{
			this.setBlockLightAt(x, y, z, 0);

			if (!visited.containsKey(index))
			{
				visited.put(index, true);
				if (current > 1)
				{
					queue.add(new Object[] { new Vector3(x, y, z), current });
				}
			}
		} else if (current >= currentLight)
		{
			if (!spreadVisited.containsKey(index))
			{
				spreadVisited.put(index, true);
				spreadQueue.add(new Vector3(x, y, z));
			}
		}
	}

	private void computeSpreadBlockLight(
		int x,
		int y,
		int z,
		int currentLight,
		Queue<Vector3> queue,
		Map<BlockVector3, Boolean> visited
	)
	{
		int current = this.getBlockLightAt(x, y, z);
		BlockVector3 index = Level.blockHash(x, y, z);

		if (current < currentLight)
		{
			this.setBlockLightAt(x, y, z, currentLight);

			if (!visited.containsKey(index))
			{
				visited.put(index, true);
				if (currentLight > 1)
				{
					queue.add(new Vector3(x, y, z));
				}
			}
		}
	}

	public boolean setBlock(
		Vector3 pos,
		Block block
	)
	{
		return this.setBlock(pos, block, false);
	}

	public boolean setBlock(
		Vector3 pos,
		Block block,
		boolean direct
	)
	{
		return this.setBlock(pos, block, direct, true);
	}

	public boolean setBlock(
		Vector3 pos,
		Block block,
		boolean direct,
		boolean update
	)
	{
		if (pos.y < 0 || pos.y >= 256)
		{
			return false;
		}

		if (this.getChunk((int) pos.x >> 4, (int) pos.z >> 4, true).setBlock((int) pos.x & 0x0f, (int) pos.y & 0xff,
			(int) pos.z & 0x0f, block.getId(), block.getDamage()
		))
		{
			Position position;
			if (!(pos instanceof Position))
			{
				position = this.temporalPosition.setComponents(pos.x, pos.y, pos.z);
			} else
			{
				position = (Position) pos;
			}

			block.position(position);
			this.blockCache.remove(Level.blockHash((int) position.x, (int) position.y, (int) position.z));

			Long index = Level.chunkHash((int) position.x >> 4, (int) position.z >> 4);

			if (direct)
			{
				this.sendBlocks(this.getChunkPlayers((int) position.x >> 4, (int) position.z >> 4).values().stream()
					.toArray(Player[]::new), new Block[] { block }, UpdateBlockPacket.FLAG_ALL_PRIORITY);
				this.chunkCache.remove(index);
			} else
			{
				addBlockChange(index, (int) block.x, (int) block.y, (int) block.z);
			}

			for (ChunkLoader loader : this.getChunkLoaders((int) position.x >> 4, (int) position.z >> 4))
			{
				loader.onBlockChanged(block);
			}

			if (update)
			{
				this.updateAllLight(block);
				BlockUpdateEvent ev = new BlockUpdateEvent(block);
				this.server.getPluginManager().callEvent(ev);
				if (!ev.isCancelled())
				{
					for (Entity entity : this.getNearbyEntities(new AxisAlignedBB(block.x - 1, block.y - 1, block.z - 1,
						block.x + 1, block.y + 1, block.z + 1
					)))
					{
						entity.scheduleUpdate();
					}
					ev.getBlock().onUpdate(BLOCK_UPDATE_NORMAL);
				}

				this.updateAround(position);
			}

			return true;
		}

		return false;
	}

	private void addBlockChange(
		int x,
		int y,
		int z
	)
	{
		long index = Level.chunkHash(x >> 4, z >> 4);
		addBlockChange(index, x, y, z);
	}

	private void addBlockChange(
		long index,
		int x,
		int y,
		int z
	)
	{
		SoftReference<Map<Short, Object>> current = changedBlocks.computeIfAbsent(index, k -> new SoftReference(new HashMap<>()));
		Map<Short, Object> currentMap = current.get();
		if (currentMap != changeBlocksFullMap && currentMap != null)
		{
			if (currentMap.size() > MAX_BLOCK_CACHE)
			{
				this.changedBlocks.put(index, new SoftReference(changeBlocksFullMap));
			} else
			{
				currentMap.put(Level.localBlockHash(x, y, z), changeBlocksPresent);
			}
		}
	}

	public void dropItem(
		Vector3 source,
		Item item
	)
	{
		this.dropItem(source, item, null);
	}

	public void dropItem(
		Vector3 source,
		Item item,
		Vector3 motion
	)
	{
		this.dropItem(source, item, motion, 10);
	}

	public void dropItem(
		Vector3 source,
		Item item,
		Vector3 motion,
		int delay
	)
	{
		this.dropItem(source, item, motion, false, delay);
	}

	public void dropItem(
		Vector3 source,
		Item item,
		Vector3 motion,
		boolean dropAround,
		int delay
	)
	{
		if (motion == null)
		{
			if (dropAround)
			{
				float f = this.rand.nextFloat() * 0.5f;
				float f1 = this.rand.nextFloat() * ((float) Math.PI * 2);

				motion = new Vector3(-MathHelper.sin(f1) * f, 0.20000000298023224, MathHelper.cos(f1) * f);
			} else
			{
				motion = new Vector3(new java.util.Random().nextDouble() * 0.2 - 0.1, 0.2,
					new java.util.Random().nextDouble() * 0.2 - 0.1
				);
			}
		}

		CompoundTag itemTag = NBTIO.putItemHelper(item);
		itemTag.setName("Item");

		if (item.getId() > 0 && item.getCount() > 0)
		{
			EntityItem itemEntity = new EntityItem(
				this.getChunk((int) source.getX() >> 4, (int) source.getZ() >> 4, true),
				new CompoundTag().putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", source.getX()))
						.add(new DoubleTag("", source.getY())).add(new DoubleTag("", source.getZ())))

					.putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", motion.x))
						.add(new DoubleTag("", motion.y)).add(new DoubleTag("", motion.z)))

					.putList(new ListTag<FloatTag>("Rotation")
						.add(new FloatTag("", new java.util.Random().nextFloat() * 360))
						.add(new FloatTag("", 0)))

					.putShort("Health", 5).putCompound("Item", itemTag).putShort("PickupDelay", delay)
			);

			itemEntity.spawnToAll();
		}
	}

	public Item useBreakOn(Vector3 vector)
	{
		return this.useBreakOn(vector, null);
	}

	public Item useBreakOn(
		Vector3 vector,
		Item item
	)
	{
		return this.useBreakOn(vector, item, null);
	}

	public Item useBreakOn(
		Vector3 vector,
		Item item,
		Player player
	)
	{
		return this.useBreakOn(vector, item, player, false);
	}

	public Item useBreakOn(
		Vector3 vector,
		Item item,
		Player player,
		boolean createParticles
	)
	{
		if (player != null && player.getGamemode() > 1)
		{
			return null;
		}
		Block target = this.getBlock(vector);
		Item[] drops;
		if (item == null)
		{
			item = new ItemBlock(new BlockAir(), 0, 0);
		}

		if (player != null)
		{
			double breakTime = target.getBreakTime(item, player);
			// this in
			// block
			// class

			if (player.isCreative() && breakTime > 0.15)
			{
				breakTime = 0.15;
			}

			if (player.hasEffect(Effect.SWIFTNESS))
			{
				breakTime *= 1 - (0.2 * (player.getEffect(Effect.SWIFTNESS).getAmplifier() + 1));
			}

			if (player.hasEffect(Effect.MINING_FATIGUE))
			{
				breakTime *= 1 - (0.3 * (player.getEffect(Effect.MINING_FATIGUE).getAmplifier() + 1));
			}

			Enchantment eff = item.getEnchantment(Enchantment.ID_EFFICIENCY);

			if (eff != null && eff.getLevel() > 0)
			{
				breakTime *= 1 - (0.3 * eff.getLevel());
			}

			breakTime -= 0.1;

			BlockBreakEvent ev = new BlockBreakEvent(player, target, item, player.isCreative(),
				(player.lastBreak + breakTime) > System.currentTimeMillis()
			);
			double distance;
			if (player.isSurvival() && !target.isBreakable(item))
			{
				ev.setCancelled();
			} else if (!player.isOp() && (distance = this.server.getSpawnRadius()) > -1)
			{
				Vector2 t = new Vector2(target.x, target.z);
				Vector2 s = new Vector2(this.getSpawnLocation().x, this.getSpawnLocation().z);
				if (!this.server.getOps().getAll().isEmpty() && t.distance(s) <= distance)
				{
					ev.setCancelled();
				}
			}

			this.server.getPluginManager().callEvent(ev);
			if (ev.isCancelled())
			{
				return null;
			}

			if (!ev.getInstaBreak() && ev.isFastBreak())
			{
				return null;
			}

			player.lastBreak = System.currentTimeMillis();

			drops = ev.getDrops();
		} else if (!target.isBreakable(item))
		{
			return null;
		} else
		{
			drops = target.getDrops(item);
		}

		Block above = this.getBlock(new Vector3(target.x, target.y + 1, target.z));
		if (above != null)
		{
			if (above.getId() == Item.FIRE)
			{
				this.setBlock(above, new BlockAir(), true);
			}
		}

		Tag tag = item.getNamedTagEntry("CanDestroy");
		if (tag instanceof ListTag)
		{
			boolean canBreak = false;
			for (Tag v : ((ListTag<Tag>) tag).getAll())
			{
				if (v instanceof StringTag)
				{
					Item entry = Item.fromString(((StringTag) v).data);
					if (entry.getId() > 0 && entry.getBlock() != null && entry.getBlock().getId() == target.getId())
					{
						canBreak = true;
						break;
					}
				}
			}

			if (!canBreak)
			{
				return null;
			}
		}

		if (createParticles)
		{
			Map<Integer, Player> players = this.getChunkPlayers((int) target.x >> 4, (int) target.z >> 4);

			this.addParticle(new DestroyBlockParticle(target.add(0.5), target), players.values());

			if (player != null)
			{
				players.remove(player.getLoaderId());
			}
		}

		target.onBreak(item);

		BlockEntity blockEntity = this.getBlockEntity(target);
		if (blockEntity != null)
		{
			if (blockEntity instanceof InventoryHolder)
			{
				if (blockEntity instanceof BlockEntityChest)
				{
					((BlockEntityChest) blockEntity).unpair();
				}

				if (player != null && player.getTransactionGroup() != null)
				{
					player.getTransactionGroup().execute();
				}
				if (blockEntity instanceof BlockEntityShulkerBox)
				{
					this.dropItem(target, target.toItem());
				} else
				{
					for (Item chestItem : ((InventoryHolder) blockEntity).getInventory().getContents().values())
					{
						((InventoryHolder) blockEntity).getInventory().dropContents(this, target);
					}
				}
			}

			blockEntity.close();

			this.updateComparatorOutputLevel(target);
		}

		item.useOn(target);
		if (item.isTool() && item.getDamage() >= item.getMaxDurability())
		{
			item = new ItemBlock(new BlockAir(), 0, 0);
		}

		if (this.gameRules.getBoolean("doTileDrops"))
		{
			int dropExp = target.getDropExp();
			if (player != null)
			{
				player.addExperience(dropExp);
				if (player.isSurvival())
				{
					for (int ii = 1 ; ii <= dropExp ; ii++)
					{
						this.dropExpOrb(target, 1);
					}
				}
			}

			if (player == null || player.isSurvival())
			{
				for (Item drop : drops)
				{
					if (drop.getCount() > 0)
					{
						this.dropItem(vector.add(0.5, 0.5, 0.5), drop);
					}
				}
			}
		}

		return item;
	}

	public void dropExpOrb(
		Vector3 source,
		int exp
	)
	{
		dropExpOrb(source, exp, null);
	}

	public void dropExpOrb(
		Vector3 source,
		int exp,
		Vector3 motion
	)
	{
		dropExpOrb(source, exp, motion, 10);
	}

	public void dropExpOrb(
		Vector3 source,
		int exp,
		Vector3 motion,
		int delay
	)
	{
		motion = (motion == null) ? new Vector3(new java.util.Random().nextDouble() * 0.2 - 0.1, 0.2,
			new java.util.Random().nextDouble() * 0.2 - 0.1
		) : motion;
		CompoundTag nbt = new CompoundTag()
			.putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", source.getX()))
				.add(new DoubleTag("", source.getY())).add(new DoubleTag("", source.getZ())))
			.putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", motion.getX()))
				.add(new DoubleTag("", motion.getY())).add(new DoubleTag("", motion.getZ())))
			.putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", 0)).add(new FloatTag("", 0)));
		Entity entity = new EntityXPOrb(this.getChunk(source.getFloorX() >> 4, source.getFloorZ() >> 4), nbt);
		EntityXPOrb xpOrb = (EntityXPOrb) entity;
		xpOrb.setExp(exp);
		xpOrb.setPickupDelay(delay);
		xpOrb.saveNBT();

		xpOrb.spawnToAll();

	}

	public Item useItemOn(
		Vector3 vector,
		Item item,
		BlockFace face,
		float fx,
		float fy,
		float fz
	)
	{
		return this.useItemOn(vector, item, face, fx, fy, fz, null);
	}

	public Item useItemOn(
		Vector3 vector,
		Item item,
		BlockFace face,
		float fx,
		float fy,
		float fz,
		Player player
	)
	{
		return this.useItemOn(vector, item, face, fx, fy, fz, player, false);
	}

	public Item useItemOn(
		Vector3 vector,
		Item item,
		BlockFace face,
		float fx,
		float fy,
		float fz,
		Player player,
		boolean playSound
	)
	{
		Block target = this.getBlock(vector);
		Block block = target.getSide(face);

		if (block.y > 255 || block.y < 0)
		{
			return null;
		}

		if (block.y > 127 && this.getDimension() == DIMENSION_NETHER)
		{
			return null;
		}

		if (target.getId() == Item.AIR)
		{
			return null;
		}

		if (player != null)
		{
			PlayerInteractEvent ev = new PlayerInteractEvent(player, item, target, face,
				target.getId() == 0 ? Action.RIGHT_CLICK_AIR : Action.RIGHT_CLICK_BLOCK
			);

			if (player.getGamemode() > 2)
			{
				ev.setCancelled();
			}

			int distance = this.server.getSpawnRadius();
			if (!player.isOp() && distance > -1)
			{
				Vector2 t = new Vector2(target.x, target.z);
				Vector2 s = new Vector2(this.getSpawnLocation().x, this.getSpawnLocation().z);
				if (!this.server.getOps().getAll().isEmpty() && t.distance(s) <= distance)
				{
					ev.setCancelled();
				}
			}

			this.server.getPluginManager().callEvent(ev);
			if (!ev.isCancelled())
			{
				target.onUpdate(BLOCK_UPDATE_TOUCH);
				if ((!player.isSneaking() || player.getInventory().getItemInHand().isNull()) && target.canBeActivated() && target.onActivate(item, player))
				{
					if (item.isTool() && item.getDamage() >= item.getMaxDurability())
					{
						item = new ItemBlock(Block.get(BlockIds.AIR), 0, 0);
					}
					return item;
				}

				if (item.canBeActivated() && item.onActivate(this, player, block, target, face, fx, fy, fz))
				{
					if (item.getCount() <= 0)
					{
						item = new ItemBlock(Block.get(BlockIds.AIR), 0, 0);
						return item;
					}
				}
			} else
			{
				return null;
			}

		} else if (target.canBeActivated() && target.onActivate(item, player))
		{
			if (item.isTool() && item.getDamage() >= item.getMaxDurability())
			{
				item = new ItemBlock(Block.get(BlockIds.AIR), 0, 0);
			}
			return item;
		}
		Block hand;
		if (item.canBePlaced())
		{
			hand = item.getBlock();
			hand.position(block);
		} else
		{
			return null;
		}

		if (!(block.canBeReplaced() || (hand.getId() == Item.SLAB && block.getId() == Item.SLAB)))
		{
			return null;
		}

		if (target.canBeReplaced())
		{
			block = target;
			hand.position(block);
		}

		if (!hand.canPassThrough() && hand.getBoundingBox() != null)
		{
			Entity[] entities = this.getCollidingEntities(hand.getBoundingBox());
			int realCount = 0;
			for (Entity e : entities)
			{
				if (e instanceof EntityArrow || e instanceof EntityItem || (e instanceof Player && ((Player) e).isSpectator()))
				{
					continue;
				}
				++realCount;
			}

			if (player != null)
			{
				Vector3 diff = player.getNextPosition().subtract(player.getPosition());
				if (diff.lengthSquared() > 0.00001)
				{
					AxisAlignedBB bb = player.getBoundingBox().getOffsetBoundingBox(diff.x, diff.y, diff.z);
					if (hand.getBoundingBox().intersectsWith(bb))
					{
						++realCount;
					}
				}
			}

			if (realCount > 0)
			{
				return null; // Entity in block
			}
		}

		if (player != null)
		{
			BlockPlaceEvent event = new BlockPlaceEvent(player, hand, block, target, item);
			if (player.getGamemode() == 2)
			{
				Tag tag = item.getNamedTagEntry("CanPlaceOn");
				boolean canPlace = false;
				if (tag instanceof ListTag)
				{
					for (Tag v : ((ListTag<Tag>) tag).getAll())
					{
						if (v instanceof StringTag)
						{
							Item entry = Item.fromString(((StringTag) v).data);
							if (entry.getId() > 0 && entry.getBlock() != null && entry.getBlock().getId() == target.getId())
							{
								canPlace = true;
								break;
							}
						}
					}
				}
				if (!canPlace)
				{
					event.setCancelled();
				}
			}
			int distance = this.server.getSpawnRadius();
			if (!player.isOp() && distance > -1)
			{
				Vector2 t = new Vector2(target.x, target.z);
				Vector2 s = new Vector2(this.getSpawnLocation().x, this.getSpawnLocation().z);
				if (!this.server.getOps().getAll().isEmpty() && t.distance(s) <= distance)
				{
					event.setCancelled();
				}
			}
			this.server.getPluginManager().callEvent(event);
			if (event.isCancelled())
			{
				return null;
			}
		}

		if (!hand.place(item, block, target, face, fx, fy, fz, player))
		{
			return null;
		}

		if (player != null)
		{
			if (!player.isCreative())
			{
				item.setCount(item.getCount() - 1);
			}
		}

		if (playSound)
		{
			this.addSound(new BlockPlaceSound(hand, hand.getId()));
		}

		if (item.getCount() <= 0)
		{
			item = new ItemBlock(Block.get(BlockIds.AIR), 0, 0);
		}
		return item;
	}

	public Entity getEntity(long entityId)
	{
		return this.entities.containsKey(entityId) ? this.entities.get(entityId) : null;
	}

	public Entity[] getEntities()
	{
		return entities.values().stream().toArray(Entity[]::new);
	}

	public Entity[] getCollidingEntities(AxisAlignedBB bb)
	{
		return this.getCollidingEntities(bb, null);
	}

	public Entity[] getCollidingEntities(
		AxisAlignedBB bb,
		Entity entity
	)
	{
		List<Entity> nearby = new ArrayList<>();

		if (entity == null || entity.canCollide())
		{
			int minX = NukkitMath.floorDouble((bb.minX - 2) / 16);
			int maxX = NukkitMath.ceilDouble((bb.maxX + 2) / 16);
			int minZ = NukkitMath.floorDouble((bb.minZ - 2) / 16);
			int maxZ = NukkitMath.ceilDouble((bb.maxZ + 2) / 16);

			for (int x = minX ; x <= maxX ; ++x)
			{
				for (int z = minZ ; z <= maxZ ; ++z)
				{
					for (Entity ent : this.getChunkEntities(x, z).values())
					{
						if ((entity == null || (ent != entity && entity.canCollideWith(ent)))
						    && ent.boundingBox.intersectsWith(bb))
						{
							nearby.add(ent);
						}
					}
				}
			}
		}

		return nearby.stream().toArray(Entity[]::new);
	}

	public Entity[] getNearbyEntities(AxisAlignedBB bb)
	{
		return this.getNearbyEntities(bb, null);
	}

	public Entity[] getNearbyEntities(
		AxisAlignedBB bb,
		Entity entity
	)
	{
		List<Entity> nearby = new ArrayList<>();

		int minX = NukkitMath.floorDouble((bb.minX - 2) / 16);
		int maxX = NukkitMath.ceilDouble((bb.maxX + 2) / 16);
		int minZ = NukkitMath.floorDouble((bb.minZ - 2) / 16);
		int maxZ = NukkitMath.ceilDouble((bb.maxZ + 2) / 16);

		for (int x = minX ; x <= maxX ; ++x)
		{
			for (int z = minZ ; z <= maxZ ; ++z)
			{
				for (Entity ent : this.getChunkEntities(x, z).values())
				{
					if (ent != entity && ent.boundingBox.intersectsWith(bb))
					{
						nearby.add(ent);
					}
				}
			}
		}

		return nearby.stream().toArray(Entity[]::new);
	}

	public Map<Long, BlockEntity> getBlockEntities()
	{
		return blockEntities;
	}

	public BlockEntity getBlockEntityById(long blockEntityId)
	{
		return this.blockEntities.containsKey(blockEntityId) ? this.blockEntities.get(blockEntityId) : null;
	}

	public Map<Long, Player> getPlayers()
	{
		return players;
	}

	public Map<Integer, ChunkLoader> getLoaders()
	{
		return loaders;
	}

	public BlockEntity getBlockEntity(Vector3 pos)
	{
		FullChunk chunk = this.getChunk((int) pos.x >> 4, (int) pos.z >> 4, false);

		if (chunk != null)
		{
			return chunk.getTile((int) pos.x & 0x0f, (int) pos.y & 0xff, (int) pos.z & 0x0f);
		}

		return null;
	}

	public Map<Long, Entity> getChunkEntities(
		int X,
		int Z
	)
	{
		FullChunk chunk;
		return (chunk = this.getChunk(X, Z)) != null ? chunk.getEntities() : new HashMap<>();
	}

	public Map<Long, BlockEntity> getChunkBlockEntities(
		int X,
		int Z
	)
	{
		FullChunk chunk;
		return (chunk = this.getChunk(X, Z)) != null ? chunk.getBlockEntities() : new HashMap<>();
	}

	@Override
	public int getBlockIdAt(
		int x,
		int y,
		int z
	)
	{
		return this.getChunk(x >> 4, z >> 4, true).getBlockId(x & 0x0f, y & 0xff, z & 0x0f);
	}

	@Override
	public void setBlockIdAt(
		int x,
		int y,
		int z,
		int id
	)
	{
		this.blockCache.remove(Level.blockHash(x, y, z));
		this.getChunk(x >> 4, z >> 4, true).setBlockId(x & 0x0f, y & 0xff, z & 0x0f, id & 0xff);
		addBlockChange(x, y, z);
		temporalVector.setComponents(x, y, z);
		for (ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4))
		{
			loader.onBlockChanged(temporalVector);
		}
	}

	public int getBlockExtraDataAt(
		int x,
		int y,
		int z
	)
	{
		return this.getChunk(x >> 4, z >> 4, true).getBlockExtraData(x & 0x0f, y & 0xff, z & 0x0f);
	}

	public void setBlockExtraDataAt(
		int x,
		int y,
		int z,
		int id,
		int data
	)
	{
		this.getChunk(x >> 4, z >> 4, true).setBlockExtraData(x & 0x0f, y & 0xff, z & 0x0f, (data << 8) | id);

		this.sendBlockExtraData(x, y, z, id, data);
	}

	@Override
	public int getBlockDataAt(
		int x,
		int y,
		int z
	)
	{
		return this.getChunk(x >> 4, z >> 4, true).getBlockData(x & 0x0f, y & 0xff, z & 0x0f);
	}

	@Override
	public void setBlockDataAt(
		int x,
		int y,
		int z,
		int data
	)
	{
		this.blockCache.remove(Level.blockHash(x, y, z));
		this.getChunk(x >> 4, z >> 4, true).setBlockData(x & 0x0f, y & 0xff, z & 0x0f, data & 0x0f);
		addBlockChange(x, y, z);
		temporalVector.setComponents(x, y, z);
		for (ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4))
		{
			loader.onBlockChanged(temporalVector);
		}
	}

	public int getBlockSkyLightAt(
		int x,
		int y,
		int z
	)
	{
		return this.getChunk(x >> 4, z >> 4, true).getBlockSkyLight(x & 0x0f, y & 0xff, z & 0x0f);
	}

	public void setBlockSkyLightAt(
		int x,
		int y,
		int z,
		int level
	)
	{
		this.getChunk(x >> 4, z >> 4, true).setBlockSkyLight(x & 0x0f, y & 0xff, z & 0x0f, level & 0x0f);
	}

	public int getBlockLightAt(
		int x,
		int y,
		int z
	)
	{
		return this.getChunk(x >> 4, z >> 4, true).getBlockLight(x & 0x0f, y & 0xff, z & 0x0f);
	}

	public void setBlockLightAt(
		int x,
		int y,
		int z,
		int level
	)
	{
		this.getChunk(x >> 4, z >> 4, true).setBlockLight(x & 0x0f, y & 0xff, z & 0x0f, level & 0x0f);
	}

	public int getBiomeId(
		int x,
		int z
	)
	{
		return this.getChunk(x >> 4, z >> 4, true).getBiomeId(x & 0x0f, z & 0x0f);
	}

	public void setBiomeId(
		int x,
		int z,
		int biomeId
	)
	{
		this.getChunk(x >> 4, z >> 4, true).setBiomeId(x & 0x0f, z & 0x0f, biomeId & 0x0f);
	}

	public int getHeightMap(
		int x,
		int z
	)
	{
		return this.getChunk(x >> 4, z >> 4, true).getHeightMap(x & 0x0f, z & 0x0f);
	}

	public void setHeightMap(
		int x,
		int z,
		int value
	)
	{
		this.getChunk(x >> 4, z >> 4, true).setHeightMap(x & 0x0f, z & 0x0f, value & 0x0f);
	}

	public int[] getBiomeColor(
		int x,
		int z
	)
	{
		return this.getChunk(x >> 4, z >> 4, true).getBiomeColor(x & 0x0f, z & 0x0f);
	}

	public void setBiomeColor(
		int x,
		int z,
		int R,
		int G,
		int B
	)
	{
		this.getChunk(x >> 4, z >> 4, true).setBiomeColor(x & 0x0f, z & 0x0f, R, G, B);
	}

	public Map<Long, BaseFullChunk> getChunks()
	{
		return chunks;
	}

	@Override
	public BaseFullChunk getChunk(
		int chunkX,
		int chunkZ
	)
	{
		return this.getChunk(chunkX, chunkZ, false);
	}

	public BaseFullChunk getChunk(
		int chunkX,
		int chunkZ,
		boolean create
	)
	{
		Long index = Level.chunkHash(chunkX, chunkZ);
		if (this.chunks.containsKey(index))
		{
			return this.chunks.get(index);
		} else if (this.loadChunk(chunkX, chunkZ, create))
		{
			return this.chunks.get(index);
		}

		return null;
	}

	public void generateChunkCallback(
		int x,
		int z,
		BaseFullChunk chunk
	)
	{
		Long index = Level.chunkHash(x, z);
		if (this.chunkPopulationQueue.containsKey(index))
		{
			FullChunk oldChunk = this.getChunk(x, z, false);
			for (int xx = -1 ; xx <= 1 ; ++xx)
			{
				for (int zz = -1 ; zz <= 1 ; ++zz)
				{
					this.chunkPopulationLock.remove(Level.chunkHash(x + xx, z + zz));
				}
			}
			this.chunkPopulationQueue.remove(index);
			chunk.setProvider(this.provider);
			this.setChunk(x, z, chunk, false);
			chunk = this.getChunk(x, z, false);
			if (chunk != null && (oldChunk == null || !oldChunk.isPopulated()) && chunk.isPopulated()
			    && chunk.getProvider() != null)
			{
				this.server.getPluginManager().callEvent(new ChunkPopulateEvent(chunk));

				for (ChunkLoader loader : this.getChunkLoaders(x, z))
				{
					loader.onChunkPopulated(chunk);
				}
			}
		} else if (this.chunkGenerationQueue.containsKey(index) || this.chunkPopulationLock.containsKey(index))
		{
			this.chunkGenerationQueue.remove(index);
			this.chunkPopulationLock.remove(index);
			chunk.setProvider(this.provider);
			this.setChunk(x, z, chunk, false);
		} else
		{
			chunk.setProvider(this.provider);
			this.setChunk(x, z, chunk, false);
		}
	}

	@Override
	public void setChunk(
		int chunkX,
		int chunkZ
	)
	{
		this.setChunk(chunkX, chunkZ, null);
	}

	@Override
	public void setChunk(
		int chunkX,
		int chunkZ,
		BaseFullChunk chunk
	)
	{
		this.setChunk(chunkX, chunkZ, chunk, true);
	}

	public void setChunk(
		int chunkX,
		int chunkZ,
		BaseFullChunk chunk,
		boolean unload
	)
	{
		if (chunk == null)
		{
			return;
		}

		Long index = Level.chunkHash(chunkX, chunkZ);
		FullChunk oldChunk = this.getChunk(chunkX, chunkZ, false);
		if (unload && oldChunk != null)
		{
			this.unloadChunk(chunkX, chunkZ, false, false);

			this.provider.setChunk(chunkX, chunkZ, chunk);
			this.chunks.put(index, chunk);
		} else
		{
			Map<Long, Entity> oldEntities = oldChunk != null ? oldChunk.getEntities() : new HashMap<>();

			Map<Long, BlockEntity> oldBlockEntities = oldChunk != null ? oldChunk.getBlockEntities() : new HashMap<>();

			for (Entity entity : oldEntities.values())
			{
				chunk.addEntity(entity);
				if (oldChunk != null)
				{
					oldChunk.removeEntity(entity);
					entity.chunk = chunk;
				}
			}

			for (BlockEntity blockEntity : oldBlockEntities.values())
			{
				chunk.addBlockEntity(blockEntity);
				if (oldChunk != null)
				{
					oldChunk.removeBlockEntity(blockEntity);
					blockEntity.chunk = chunk;
				}
			}

			this.provider.setChunk(chunkX, chunkZ, chunk);
			this.chunks.put(index, chunk);
		}

		this.chunkCache.remove(index);
		chunk.setChanged();

		if (!this.isChunkInUse(chunkX, chunkZ))
		{
			this.unloadChunkRequest(chunkX, chunkZ);
		} else
		{
			for (ChunkLoader loader : this.getChunkLoaders(chunkX, chunkZ))
			{
				loader.onChunkChanged(chunk);
			}
		}
	}

	public int getHighestBlockAt(
		int x,
		int z
	)
	{
		return this.getChunk(x >> 4, z >> 4, true).getHighestBlockAt(x & 0x0f, z & 0x0f);
	}

	public BlockColor getMapColorAt(
		int x,
		int z
	)
	{
		int y = getHighestBlockAt(x, z);
		while (y > 1)
		{
			Block block = getBlock(new Vector3(x, y, z));
			BlockColor blockColor = block.getColor();
			if (blockColor.getAlpha() == 0x00)
			{
				y--;
			} else
			{
				return blockColor;
			}
		}
		return BlockColor.VOID_BLOCK_COLOR;
	}

	public boolean isChunkLoaded(
		int x,
		int z
	)
	{
		return this.chunks.containsKey(Level.chunkHash(x, z)) || this.provider.isChunkLoaded(x, z);
	}

	public boolean isChunkGenerated(
		int x,
		int z
	)
	{
		FullChunk chunk = this.getChunk(x, z);
		return chunk != null && chunk.isGenerated();
	}

	public boolean isChunkPopulated(
		int x,
		int z
	)
	{
		FullChunk chunk = this.getChunk(x, z);
		return chunk != null && chunk.isPopulated();
	}

	public Position getSpawnLocation()
	{
		return Position.fromObject(this.provider.getSpawn(), this);
	}

	public void setSpawnLocation(Vector3 pos)
	{
		Position previousSpawn = this.getSpawnLocation();
		this.provider.setSpawn(pos);
		this.server.getPluginManager().callEvent(new SpawnChangeEvent(this, previousSpawn));
		SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
		pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
		pk.x = pos.getFloorX();
		pk.y = pos.getFloorY();
		pk.z = pos.getFloorZ();
		for (Player p : getPlayers().values()) p.dataPacket(pk);
	}

	public void requestChunk(
		int x,
		int z,
		Player player
	)
	{
		Long index = Level.chunkHash(x, z);
		if (player.getGamemode() == Player.SPECTATOR && !this.gameRules.getBoolean("spectatorsGenerateChunks") && isChunkGenerated(x, z))
		{
			return;
		}

		if (!this.chunkSendQueue.containsKey(index))
		{
			this.chunkSendQueue.put(index, new HashMap<>());
		}

		this.chunkSendQueue.get(index).put(player.getLoaderId(), player);
	}

	private void sendChunkFromCache(
		int x,
		int z
	)
	{
		Long index = Level.chunkHash(x, z);
		if (this.chunkSendTasks.containsKey(index))
		{
			for (Player player : this.chunkSendQueue.get(index).values())
			{
				if (player.isConnected() && player.usedChunks.containsKey(index))
				{
					player.sendChunk(x, z, (DataPacket) this.chunkCache.get(index));
				}
			}

			this.chunkSendQueue.remove(index);
			this.chunkSendTasks.remove(index);
		}
	}

	private void processChunkRequest()
	{
		if (!this.chunkSendQueue.isEmpty())
		{
			for (Long index : new ArrayList<>(this.chunkSendQueue.keySet()))
			{
				if (this.chunkSendTasks.containsKey(index))
				{
					continue;
				}
				int x = getHashX(index);
				int z = getHashZ(index);
				this.chunkSendTasks.put(index, true);
				if (this.chunkCache.containsKey(index))
				{
					this.sendChunkFromCache(x, z);
					continue;
				}
				AsyncTask task = this.provider.requestChunkTask(x, z);
				if (task != null)
				{
					this.server.getScheduler().scheduleAsyncTask(task);
				}
			}
		}
	}

	public void chunkRequestCallback(
		int x,
		int z,
		byte[] payload
	)
	{
		Long index = Level.chunkHash(x, z);

		if (this.cacheChunks && !this.chunkCache.containsKey(index))
		{
			this.chunkCache.put(index, Player.getChunkCacheFromData(x, z, payload));
			this.sendChunkFromCache(x, z);
			return;
		}

		if (this.chunkSendTasks.containsKey(index))
		{
			for (Player player : this.chunkSendQueue.get(index).values())
			{
				if (player.isConnected() && player.usedChunks.containsKey(index))
				{
					player.sendChunk(x, z, payload);
				}
			}

			this.chunkSendQueue.remove(index);
			this.chunkSendTasks.remove(index);
		}
	}

	public void removeEntity(Entity entity)
	{
		if (entity.getLevel() != this)
		{
			throw new LevelException("Invalid Entity level");
		}

		if (entity instanceof Player)
		{
			this.players.remove(entity.getId());
			this.checkSleep();
		} else
		{
			entity.close();
		}

		this.entities.remove(entity.getId());
		this.updateEntities.remove(entity.getId());
	}

	public void addEntity(Entity entity)
	{
		if (entity.getLevel() != this)
		{
			throw new LevelException("Invalid Entity level");
		}

		if (entity instanceof Player)
		{
			this.players.put(entity.getId(), (Player) entity);
		}
		this.entities.put(entity.getId(), entity);
	}

	public void addBlockEntity(BlockEntity blockEntity)
	{
		if (blockEntity.getLevel() != this)
		{
			throw new LevelException("Invalid Block Entity level");
		}
		blockEntities.put(blockEntity.getId(), blockEntity);
		this.clearChunkCache((int) blockEntity.getX() >> 4, (int) blockEntity.getZ() >> 4);
	}

	public void removeBlockEntity(BlockEntity blockEntity)
	{
		if (blockEntity.getLevel() != this)
		{
			throw new LevelException("Invalid Block Entity level");
		}
		blockEntities.remove(blockEntity.getId());
		updateBlockEntities.remove(blockEntity.getId());
		this.clearChunkCache((int) blockEntity.getX() >> 4, (int) blockEntity.getZ() >> 4);
	}

	public boolean isChunkInUse(
		int x,
		int z
	)
	{
		Long index = Level.chunkHash(x, z);
		return this.chunkLoaders.containsKey(index) && !this.chunkLoaders.get(index).isEmpty();
	}

	public boolean loadChunk(
		int x,
		int z
	)
	{
		return this.loadChunk(x, z, true);
	}

	public boolean loadChunk(
		int x,
		int z,
		boolean generate
	)
	{
		Long index = Level.chunkHash(x, z);
		if (this.chunks.containsKey(index))
		{
			return true;
		}

		this.cancelUnloadChunkRequest(x, z);

		BaseFullChunk chunk = this.provider.getChunk(x, z, generate);

		if (chunk == null)
		{
			if (generate)
			{
				throw new IllegalStateException("Could not create new Chunk");
			}
			return false;
		}

		this.chunks.put(index, chunk);
		chunk.initChunk();

		if (chunk.getProvider() != null)
		{
			this.server.getPluginManager().callEvent(new ChunkLoadEvent(chunk, !chunk.isGenerated()));
		} else
		{
			this.unloadChunk(x, z, false);
			return false;
		}

		if (!chunk.isLightPopulated() && chunk.isPopulated()
		    && (boolean) this.getServer().getConfig("chunk-ticking.light-updates", false))
		{
			this.getServer().getScheduler().scheduleAsyncTask(new LightPopulationTask(this, chunk));
		}

		if (this.isChunkInUse(x, z))
		{
			for (ChunkLoader loader : this.getChunkLoaders(x, z))
			{
				loader.onChunkLoaded(chunk);
			}
		} else
		{
			this.unloadChunkRequest(x, z);
		}
		return true;
	}

	private void queueUnloadChunk(
		int x,
		int z
	)
	{
		Long index = Level.chunkHash(x, z);
		this.unloadQueue.put(index, System.currentTimeMillis());
		this.chunkTickList.remove(index);
	}

	public boolean unloadChunkRequest(
		int x,
		int z
	)
	{
		return this.unloadChunkRequest(x, z, true);
	}

	public boolean unloadChunkRequest(
		int x,
		int z,
		boolean safe
	)
	{
		if ((safe && this.isChunkInUse(x, z)) || this.isSpawnChunk(x, z))
		{
			return false;
		}

		this.queueUnloadChunk(x, z);

		return true;
	}

	public void cancelUnloadChunkRequest(
		int x,
		int z
	)
	{
		this.unloadQueue.remove(Level.chunkHash(x, z));
	}

	public boolean unloadChunk(
		int x,
		int z
	)
	{
		return this.unloadChunk(x, z, true);
	}

	public boolean unloadChunk(
		int x,
		int z,
		boolean safe
	)
	{
		return this.unloadChunk(x, z, safe, true);
	}

	public boolean unloadChunk(
		int x,
		int z,
		boolean safe,
		boolean trySave
	)
	{
		if (safe && this.isChunkInUse(x, z))
		{
			return false;
		}

		if (!this.isChunkLoaded(x, z))
		{
			return true;
		}

		Long index = Level.chunkHash(x, z);

		BaseFullChunk chunk = this.getChunk(x, z);

		if (chunk != null && chunk.getProvider() != null)
		{
			ChunkUnloadEvent ev = new ChunkUnloadEvent(chunk);
			this.server.getPluginManager().callEvent(ev);
			if (ev.isCancelled())
			{
				return false;
			}
		}

		try
		{
			if (chunk != null)
			{
				if (trySave && this.getAutoSave())
				{
					int entities = 0;
					for (Entity e : chunk.getEntities().values())
					{
						if (e instanceof Player)
						{
							continue;
						}
						++entities;
					}

					if (chunk.hasChanged() || !chunk.getBlockEntities().isEmpty() || entities > 0)
					{
						this.provider.setChunk(x, z, chunk);
						this.provider.saveChunk(x, z);
					}
				}
				for (ChunkLoader loader : this.getChunkLoaders(x, z))
				{
					loader.onChunkUnloaded(chunk);
				}
			}
			this.provider.unloadChunk(x, z, safe);
		}
		catch (Exception e)
		{
			MainLogger logger = this.server.getLogger();
			logger.error(this.server.getLanguage().translateString("nukkit.level.chunkUnloadError", e.toString()));
			logger.logException(e);
		}

		this.chunks.remove(index);
		this.chunkTickList.remove(index);
		this.chunkCache.remove(index);

		return true;
	}

	public boolean isSpawnChunk(
		int X,
		int Z
	)
	{
		int spawnX = (int) this.provider.getSpawn().getX() >> 4;
		int spawnZ = (int) this.provider.getSpawn().getZ() >> 4;

		return Math.abs(X - spawnX) <= 1 && Math.abs(Z - spawnZ) <= 1;
	}

	public Position getSafeSpawn()
	{
		return this.getSafeSpawn(null);
	}

	public Position getSafeSpawn(Vector3 spawn)
	{
		if (spawn == null || spawn.y < 1)
		{
			spawn = this.getSpawnLocation();
		}

		if (spawn != null)
		{
			Vector3 v = spawn.floor();
			FullChunk chunk = this.getChunk((int) v.x >> 4, (int) v.z >> 4, false);
			int x = (int) v.x & 0x0f;
			int z = (int) v.z & 0x0f;
			if (chunk != null)
			{
				int y = (int) Math.min(254, v.y);
				boolean wasAir = chunk.getBlockId(x, y - 1, z) == 0;
				for ( ; y > 0 ; --y)
				{
					int b = chunk.getFullBlock(x, y, z);
					Block block = Block.get(b >> 4, b & 0x0f);
					if (this.isFullBlock(block))
					{
						if (wasAir)
						{
							y++;
							break;
						}
					} else
					{
						wasAir = true;
					}
				}

				for ( ; y >= 0 && y < 256 ; ++y)
				{
					int b = chunk.getFullBlock(x, y + 1, z);
					Block block = Block.get(b >> 4, b & 0x0f);
					if (!this.isFullBlock(block))
					{
						b = chunk.getFullBlock(x, y, z);
						block = Block.get(b >> 4, b & 0x0f);
						if (!this.isFullBlock(block))
						{
							return new Position(spawn.x, y == (int) spawn.y ? spawn.y : y, spawn.z, this);
						}
					} else
					{
						++y;
					}
				}

				v.y = y;
			}

			return new Position(spawn.x, v.y, spawn.z, this);
		}

		return null;
	}

	public int getTime()
	{
		return (int) time;
	}

	public void setTime(int time)
	{
		this.time = time;
		this.sendTime();
	}

	public boolean isDaytime()
	{
		return this.skyLightSubtracted < 4;
	}

	public long getCurrentTick()
	{
		return this.levelCurrentTick;
	}

	public String getName()
	{
		return this.provider.getName();
	}

	public String getFolderName()
	{
		return this.folderName;
	}

	public void stopTime()
	{
		this.stopTime = true;
		this.sendTime();
	}

	public void startTime()
	{
		this.stopTime = false;
		this.sendTime();
	}

	@Override
	public long getSeed()
	{
		return this.provider.getSeed();
	}

	public void setSeed(int seed)
	{
		this.provider.setSeed(seed);
	}

	public boolean populateChunk(
		int x,
		int z
	)
	{
		return this.populateChunk(x, z, false);
	}

	public boolean populateChunk(
		int x,
		int z,
		boolean force
	)
	{
		Long index = Level.chunkHash(x, z);
		if (this.chunkPopulationQueue.containsKey(index) || this.chunkPopulationQueue.size() >= this.chunkPopulationQueueSize && !force)
		{
			return false;
		}

		BaseFullChunk chunk = this.getChunk(x, z, true);
		boolean populate;
		if (!chunk.isPopulated())
		{
			populate = true;
			for (int xx = -1 ; xx <= 1 ; ++xx)
			{
				for (int zz = -1 ; zz <= 1 ; ++zz)
				{
					if (this.chunkPopulationLock.containsKey(Level.chunkHash(x + xx, z + zz)))
					{

						populate = false;
						break;
					}
				}
			}

			if (populate)
			{
				if (!this.chunkPopulationQueue.containsKey(index))
				{
					this.chunkPopulationQueue.put(index, true);
					for (int xx = -1 ; xx <= 1 ; ++xx)
					{
						for (int zz = -1 ; zz <= 1 ; ++zz)
						{
							this.chunkPopulationLock.put(Level.chunkHash(x + xx, z + zz), true);
						}
					}

					PopulationTask task = new PopulationTask(this, chunk);
					this.server.getScheduler().scheduleAsyncTask(task);
				}
			}
			return false;
		}

		return true;
	}

	public void generateChunk(
		int x,
		int z
	)
	{
		this.generateChunk(x, z, false);
	}

	public void generateChunk(
		int x,
		int z,
		boolean force
	)
	{
		if (this.chunkGenerationQueue.size() >= this.chunkGenerationQueueSize && !force)
		{
			return;
		}

		Long index = Level.chunkHash(x, z);
		if (!this.chunkGenerationQueue.containsKey(index))
		{
			this.chunkGenerationQueue.put(index, true);
			GenerationTask task = new GenerationTask(this, this.getChunk(x, z, true));
			this.server.getScheduler().scheduleAsyncTask(task);
		}
	}

	public void regenerateChunk(
		int x,
		int z
	)
	{
		this.unloadChunk(x, z, false);

		this.cancelUnloadChunkRequest(x, z);

		this.generateChunk(x, z);
	}

	public void doChunkGarbageCollection()
	{
		// remove all invaild block entities.
		List<BlockEntity> toClose = new ArrayList<>();
		for (BlockEntity anBlockEntity : blockEntities.values())
		{
			if (anBlockEntity == null)
				continue;
			if (anBlockEntity.isBlockEntityValid())
				continue;
			toClose.add(anBlockEntity);
		}
		for (BlockEntity be : toClose.toArray(new BlockEntity[toClose.size()]))
		{
			be.close();
		}

		for (Long index : this.chunks.keySet())
		{

			if (!this.unloadQueue.containsKey(index))
			{
				int X = getHashX(index);
				int Z = getHashZ(index);
				if (!this.isSpawnChunk(X, Z))
				{
					this.unloadChunkRequest(X, Z, true);
				}
			}
		}

		for (FullChunk chunk : new ArrayList<>(this.provider.getLoadedChunks().values()))
		{
			if (!this.chunks.containsKey(Level.chunkHash(chunk.getX(), chunk.getZ())))
			{
				this.provider.unloadChunk(chunk.getX(), chunk.getZ(), false);
			}
		}

		this.provider.doGarbageCollection();
	}

	public void unloadChunks()
	{
		this.unloadChunks(false);
	}

	public void unloadChunks(boolean force)
	{
		if (!this.unloadQueue.isEmpty())
		{
			int maxUnload = 96;
			long now = System.currentTimeMillis();

			for (Long index : new ArrayList<>(this.unloadQueue.keySet()))
			{
				long time = this.unloadQueue.get(index);

				int X = getHashX(index);
				int Z = getHashZ(index);

				if (!force)
				{
					if (maxUnload <= 0)
					{
						break;
					} else if (time > (now - 30000))
					{
						continue;
					}
				}

				if (this.unloadChunk(X, Z, true))
				{
					this.unloadQueue.remove(index);
					--maxUnload;
				}
			}
		}
	}

	@Override
	public void setMetadata(
		String metadataKey,
		MetadataValue newMetadataValue
	) throws Exception
	{
		this.server.getLevelMetadata().setMetadata(this, metadataKey, newMetadataValue);
	}

	@Override
	public List<MetadataValue> getMetadata(String metadataKey) throws Exception
	{
		return this.server.getLevelMetadata().getMetadata(this, metadataKey);
	}

	@Override
	public boolean hasMetadata(String metadataKey) throws Exception
	{
		return this.server.getLevelMetadata().hasMetadata(this, metadataKey);
	}

	@Override
	public void removeMetadata(
		String metadataKey,
		Plugin owningPlugin
	) throws Exception
	{
		this.server.getLevelMetadata().removeMetadata(this, metadataKey, owningPlugin);
	}

	public void addEntityMotion(
		int chunkX,
		int chunkZ,
		long entityId,
		double x,
		double y,
		double z
	)
	{
		SetEntityMotionPacket pk = new SetEntityMotionPacket();
		pk.eid = entityId;
		pk.motionX = (float) x;
		pk.motionY = (float) y;
		pk.motionZ = (float) z;

		this.addChunkPacket(chunkX, chunkZ, pk);
	}

	public void addEntityMovement(
		int chunkX,
		int chunkZ,
		long entityId,
		double x,
		double y,
		double z,
		double yaw,
		double pitch,
		double headYaw
	)
	{
		MoveEntityPacket pk = new MoveEntityPacket();
		pk.eid = entityId;
		pk.x = (float) x;
		pk.y = (float) y;
		pk.z = (float) z;
		pk.yaw = (float) yaw;
		pk.headYaw = (float) yaw;
		pk.pitch = (float) pitch;

		this.addChunkPacket(chunkX, chunkZ, pk);
	}

	public boolean isRaining()
	{
		return this.raining;
	}

	public boolean setRaining(boolean raining)
	{
		WeatherChangeEvent ev = new WeatherChangeEvent(this, raining);
		this.getServer().getPluginManager().callEvent(ev);

		if (ev.isCancelled())
		{
			return false;
		}

		this.raining = raining;

		LevelEventPacket pk = new LevelEventPacket();
		// These numbers are from Minecraft

		if (raining)
		{
			pk.evid = LevelEventPacket.EVENT_START_RAIN;
			pk.data = rand.nextInt(50000) + 10000;
			setRainTime(rand.nextInt(12000) + 12000);
		} else
		{
			pk.evid = LevelEventPacket.EVENT_STOP_RAIN;
			setRainTime(rand.nextInt(168000) + 12000);
		}

		Server.broadcastPacket(this.getPlayers().values(), pk);

		return true;
	}

	public int getRainTime()
	{
		return this.rainTime;
	}

	public void setRainTime(int rainTime)
	{
		this.rainTime = rainTime;
	}

	public boolean isThundering()
	{
		return isRaining() && this.thundering;
	}

	public boolean setThundering(boolean thundering)
	{
		ThunderChangeEvent ev = new ThunderChangeEvent(this, thundering);
		this.getServer().getPluginManager().callEvent(ev);

		if (ev.isCancelled())
		{
			return false;
		}

		if (thundering && !isRaining())
		{
			setRaining(true);
		}

		this.thundering = thundering;

		LevelEventPacket pk = new LevelEventPacket();
		// These numbers are from Minecraft
		if (thundering)
		{
			pk.evid = LevelEventPacket.EVENT_START_THUNDER;
			pk.data = rand.nextInt(50000) + 10000;
			setThunderTime(rand.nextInt(12000) + 3600);
		} else
		{
			pk.evid = LevelEventPacket.EVENT_STOP_THUNDER;
			setThunderTime(rand.nextInt(168000) + 12000);
		}

		Server.broadcastPacket(this.getPlayers().values(), pk);

		return true;
	}

	public int getThunderTime()
	{
		return this.thunderTime;
	}

	public void setThunderTime(int thunderTime)
	{
		this.thunderTime = thunderTime;
	}

	public void sendWeather(Player[] players)
	{
		if (players == null)
		{
			players = this.getPlayers().values().stream().toArray(Player[]::new);
		}

		LevelEventPacket pk = new LevelEventPacket();

		if (this.isRaining())
		{
			pk.evid = LevelEventPacket.EVENT_START_RAIN;
			pk.data = rand.nextInt(50000) + 10000;
		} else
		{
			pk.evid = LevelEventPacket.EVENT_STOP_RAIN;
		}

		Server.broadcastPacket(players, pk);

		if (this.isThundering())
		{
			pk.evid = LevelEventPacket.EVENT_START_THUNDER;
			pk.data = rand.nextInt(50000) + 10000;
		} else
		{
			pk.evid = LevelEventPacket.EVENT_STOP_THUNDER;
		}

		Server.broadcastPacket(players, pk);
	}

	public void sendWeather(Player player)
	{
		if (player != null)
		{
			this.sendWeather(new Player[] { player });
		}
	}

	public void sendWeather(Collection<Player> players)
	{
		if (players == null)
		{
			players = this.getPlayers().values();
		}
		this.sendWeather(players.stream().toArray(Player[]::new));
	}

	public int getDimension()
	{
		return dimension;
	}

	public boolean canBlockSeeSky(Vector3 pos)
	{
		return this.getHighestBlockAt(pos.getFloorX(), pos.getFloorZ()) < pos.getY();
	}

	public int getStrongPower(
		Vector3 pos,
		BlockFace direction
	)
	{
		return this.getBlock(pos).getStrongPower(direction);
	}

	public int getStrongPower(Vector3 pos)
	{
		int i = 0;
		i = Math.max(i, this.getStrongPower(pos.down(), BlockFace.DOWN));

		if (i >= 15)
		{
			return i;
		} else
		{
			i = Math.max(i, this.getStrongPower(pos.up(), BlockFace.UP));

			if (i >= 15)
			{
				return i;
			} else
			{
				i = Math.max(i, this.getStrongPower(pos.north(), BlockFace.NORTH));

				if (i >= 15)
				{
					return i;
				} else
				{
					i = Math.max(i, this.getStrongPower(pos.south(), BlockFace.SOUTH));

					if (i >= 15)
					{
						return i;
					} else
					{
						i = Math.max(i, this.getStrongPower(pos.west(), BlockFace.WEST));

						if (i >= 15)
						{
							return i;
						} else
						{
							i = Math.max(i, this.getStrongPower(pos.east(), BlockFace.EAST));
							return i >= 15 ? i : i;
						}
					}
				}
			}
		}
	}

	public boolean isSidePowered(
		Vector3 pos,
		BlockFace face
	)
	{
		return this.getRedstonePower(pos, face) > 0;
	}

	public int getRedstonePower(
		Vector3 pos,
		BlockFace face
	)
	{
		Block block = this.getBlock(pos);
		return block.isNormalBlock() ? this.getStrongPower(pos) : block.getWeakPower(face);
	}

	public boolean isBlockPowered(Vector3 pos)
	{
		return this.getRedstonePower(pos.north(), BlockFace.NORTH) > 0 || this.getRedstonePower(pos.south(), BlockFace.SOUTH) > 0 || this.getRedstonePower(pos.west(), BlockFace.WEST) > 0 || this.getRedstonePower(pos.east(), BlockFace.EAST) > 0 || this.getRedstonePower(pos.down(), BlockFace.DOWN) > 0 || this.getRedstonePower(pos.up(), BlockFace.UP) > 0;
	}

	public int isBlockIndirectlyGettingPowered(Vector3 pos)
	{
		int power = 0;

		for (BlockFace face : BlockFace.values())
		{
			int blockPower = this.getRedstonePower(pos.getSide(face), face);

			if (blockPower >= 15)
			{
				return 15;
			}

			if (blockPower > power)
			{
				power = blockPower;
			}
		}

		return power;
	}

	private boolean isAreaLoaded(AxisAlignedBB bb)
	{
		if (bb.maxY < 0 || bb.minY >= 256)
		{
			return false;
		}
		int minX = NukkitMath.floorDouble(bb.minX) >> 4;
		int minZ = NukkitMath.floorDouble(bb.minZ) >> 4;
		int maxX = NukkitMath.floorDouble(bb.maxX) >> 4;
		int maxZ = NukkitMath.floorDouble(bb.maxZ) >> 4;

		for (int x = minX ; x <= maxX ; ++x)
		{
			for (int z = minZ ; z <= maxZ ; ++z)
			{
				if (!this.isChunkLoaded(x, z))
				{
					return false;
				}
			}
		}

		return true;
	}

	public int getSpawnRadius()
	{
		return getGameRules().getInt("spawnRadius");
	}

}
